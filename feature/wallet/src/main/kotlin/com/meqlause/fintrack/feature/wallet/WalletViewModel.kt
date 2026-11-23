package com.meqlause.fintrack.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.domain.usecase.ObserveNetWorthUseCase
import com.meqlause.fintrack.core.domain.validation.WalletNameValidator
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import javax.inject.Inject

// FR-010..016, S-09: drives the Dompet tab; the repository is the only source of wallets and balances
@HiltViewModel
internal class WalletViewModel
    @Inject
    constructor(
        private val wallets: WalletRepository,
        private val validator: WalletNameValidator,
        private val netWorth: ObserveNetWorthUseCase,
        private val clock: Clock,
    ) : ViewModel() {
        private val editor = MutableStateFlow<WalletEditor?>(null)
        private val picker = MutableStateFlow<WalletPicker?>(null)
        private val archivedExpanded = MutableStateFlow(false)

        // S-09: Loading until the first list arrives, then Empty or Success with the total and the sections
        val state: StateFlow<WalletUiState> =
            combine(
                combine(wallets.observeAll(), wallets.observeBalances(), netWorth()) { all, balances, worth ->
                    Triple(all, balances, worth)
                },
                editor,
                picker,
                archivedExpanded,
            ) { (all, balances, worth), draft, open, expanded ->
                buildState(all, balances, worth, SheetView(draft, open, expanded))
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), WalletUiState.Loading)

        // S-09: the archived section opens and closes in place
        fun toggleArchivedSection() {
            archivedExpanded.update { !it }
        }

        // FR-010: opens the editor for a new wallet
        fun startCreate() {
            editor.value = WalletEditor.forNew()
        }

        // FR-011: opens the editor for an existing wallet
        fun startEdit(item: WalletItem) {
            editor.value = WalletEditor.forWallet(item)
        }

        // FR-010: closes the editor and any drawer it opened
        fun dismissEditor() {
            editor.value = null
            picker.value = null
        }

        fun openPicker(target: WalletPicker) {
            picker.value = target
        }

        fun dismissPicker() {
            picker.value = null
        }

        // FR-010, FR-139: one change of the draft; a drawer result also closes the drawer
        fun edit(change: (WalletEditor) -> WalletEditor) {
            editor.update { draft -> draft?.let(change) }
        }

        // FR-010, FR-170: a drawer result changes the draft and closes the drawer
        fun pick(change: (WalletEditor) -> WalletEditor) {
            edit(change)
            picker.value = null
        }

        // FR-139: the statement or due day drawer result, depending on which drawer was open
        fun pickDay(pickedDay: Int) {
            val open = picker.value
            pick { it.withDay(open, pickedDay) }
        }

        // FR-010, FR-011, FR-139: saves a new or edited wallet; a rejected name or limit keeps the editor open
        fun save() {
            val draft = editor.value ?: return
            val nameError = validator.validate(draft.name)
            val limitError = draft.isCredit && (draft.creditLimit.toLongOrNull() ?: 0L) <= 0L
            if (nameError != null || limitError) {
                editor.value = draft.withErrors(nameError, limitError)
                return
            }
            viewModelScope.launch {
                val stored = wallets.observeAll().first()
                val stamp = clock.instant()
                val existing = stored.firstOrNull { it.walletId == draft.walletId }
                if (existing == null) {
                    wallets.insert(WalletDraftMapper.newWallet(draft, WalletDraftMapper.nextSortOrder(stored), stamp))
                } else {
                    wallets.update(WalletDraftMapper.applyTo(existing, draft, stamp))
                }
                dismissEditor()
            }
        }

        // FR-012: archives or restores the wallet being edited; archived wallets leave the pickers, not history
        fun setArchivedEditing(archived: Boolean) {
            val walletId = editor.value?.walletId ?: return
            viewModelScope.launch {
                val existing = wallets.observeAll().first().firstOrNull { it.walletId == walletId } ?: return@launch
                wallets.update(WalletDraftMapper.archived(existing, archived, clock.instant()))
                dismissEditor()
            }
        }

        private fun buildState(
            allWallets: List<Wallet>,
            balances: Map<WalletId, Money>,
            worth: NetWorth,
            sheet: SheetView,
        ): WalletUiState {
            if (allWallets.isEmpty()) return WalletUiState.Empty(editor = sheet.draft, picker = sheet.open)
            val items =
                allWallets.map { WalletItem(wallet = it, balance = balances[it.walletId] ?: it.terms.initialBalance) }
            val active = items.filterNot { it.wallet.status.isArchived }
            return WalletUiState.Success(
                netWorth = worth,
                sections = WalletSection.group(active),
                archived = items.filter { it.wallet.status.isArchived },
                archivedExpanded = sheet.expanded,
                editor = sheet.draft,
                picker = sheet.open,
            )
        }

        // S-09: the editor draft, the open drawer and the archived section, as the screen shows them
        private data class SheetView(
            val draft: WalletEditor?,
            val open: WalletPicker?,
            val expanded: Boolean,
        )

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
        }
    }
