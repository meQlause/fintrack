package com.meqlause.fintrack.feature.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.detection.NotificationAccess
import com.meqlause.fintrack.core.domain.inbox.InboxChoice
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.WalletId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

// FR-084 … FR-091, S-07: drives Kotak Masuk; every confirm or dismiss goes through a use case
@HiltViewModel
internal class InboxViewModel
    @Inject
    constructor(
        private val inbox: InboxRepository,
        private val wallets: WalletRepository,
        private val categories: CategoryRepository,
        private val access: NotificationAccess,
        private val commands: InboxCommands,
        private val clock: Clock,
    ) : ViewModel() {
        private val local = MutableStateFlow(InboxLocal())

        // S-07: the list follows the stored items, the wallets and categories, and the card edits
        val state: StateFlow<InboxUiState> =
            combine(
                inbox.observeAll(),
                wallets.observeAll(),
                categories.observeAll(),
                local,
            ) { items, walletList, cats, edits ->
                val snapshot = InboxSnapshot(items, walletList, cats, hasAccess = access.isGranted())
                buildInboxState(snapshot = snapshot, local = edits, clock = clock)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), InboxUiState())

        // FR-087: Catat stores the card as it is shown; nothing is stored when the wallet or category is missing
        fun confirmCard(itemId: Long) {
            val card =
                state.value.pending.cards
                    .firstOrNull { it.itemId == itemId } ?: return
            val walletId = card.wallet.slotId?.let(::WalletId)
            val categoryId = card.category.slotId?.let(::CategoryId)
            viewModelScope.launch { commands.confirm(InboxChoice(InboxItemId(itemId), walletId, categoryId)) }
        }

        // FR-091: Catat sebagai Transfer stores one transfer for the debit and the credit
        fun confirmPair(fromItemId: Long) {
            val pair =
                state.value.pending.pairs
                    .firstOrNull { it.fromItemId == fromItemId } ?: return
            val choice =
                InboxChoice(
                    itemId = InboxItemId(pair.fromItemId),
                    walletId = pair.from.slotId?.let(::WalletId),
                    categoryId = null,
                    targetWalletId = pair.target.slotId?.let(::WalletId),
                    asTransfer = true,
                )
            viewModelScope.launch { commands.confirm(choice) }
        }

        // FR-091: Catat terpisah keeps both items and shows them as two cards
        fun splitPair(fromItemId: Long) {
            val pair =
                state.value.pending.pairs
                    .firstOrNull { it.fromItemId == fromItemId } ?: return
            local.value = local.value.copy(separated = local.value.separated + pair.fromItemId + pair.toItemId)
        }

        // FR-087: swipe left and Abaikan; the item can be restored with the undo bar
        fun dismissCard(itemId: Long) {
            local.value = local.value.copy(undoItemId = itemId)
            viewModelScope.launch { commands.dismiss(InboxItemId(itemId)) }
        }

        fun undoDismiss() {
            val itemId = local.value.undoItemId ?: return
            local.value = local.value.copy(undoItemId = null)
            viewModelScope.launch { commands.restore(InboxItemId(itemId)) }
        }

        fun clearUndo() {
            local.value = local.value.copy(undoItemId = null)
        }

        // S-07 overflow: "Abaikan semua"
        fun dismissEverything() {
            viewModelScope.launch { commands.dismissAll() }
        }

        // FR-087: the category chip changes the suggestion of one card
        fun chooseCategory(
            itemId: Long,
            categoryId: Long,
        ) = editCard(itemId) { copy(categoryId = categoryId) }

        // FR-087: the wallet chip changes the suggestion of one card
        fun chooseWallet(
            itemId: Long,
            walletId: Long,
        ) = editCard(itemId) { copy(walletId = walletId) }

        private fun editCard(
            itemId: Long,
            change: InboxEdit.() -> InboxEdit,
        ) {
            val current = local.value
            val edit = (current.edits[itemId] ?: InboxEdit()).change()
            local.value = current.copy(edits = current.edits + (itemId to edit))
        }
    }
