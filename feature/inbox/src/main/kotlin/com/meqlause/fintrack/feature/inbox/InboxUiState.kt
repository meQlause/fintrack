package com.meqlause.fintrack.feature.inbox

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.picker.PickerOption

// FR-084, FR-087, S-07: the whole Kotak Masuk screen; isLoaded stays false until the first emission
internal data class InboxUiState(
    val isLoaded: Boolean = false,
    val hasAccess: Boolean = true,
    val pending: InboxPending = InboxPending(),
    val resolved: List<InboxResolvedUi> = emptyList(),
    val options: InboxOptions = InboxOptions(),
    val undoItemId: Long? = null,
)

// S-07: the items waiting for a decision, the pairs first and the single cards after them
internal data class InboxPending(
    val cards: List<InboxCardUi> = emptyList(),
    val pairs: List<InboxPairUi> = emptyList(),
) {
    val isEmpty: Boolean get() = cards.isEmpty() && pairs.isEmpty()
}

// S-18: the choices of the wallet picker and of the expense and income category pickers
internal data class InboxOptions(
    val wallets: List<PickerOption> = emptyList(),
    val expenses: List<PickerOption> = emptyList(),
    val incomes: List<PickerOption> = emptyList(),
)

// FR-087, S-07: one card of the list; wallet and category are the values that Catat will store
internal data class InboxCardUi(
    val itemId: Long,
    val text: InboxCardText,
    val amount: Money,
    val type: TransactionType,
    val wallet: InboxSlot,
    val category: InboxSlot,
)

// S-07: the words of one card, the source app, the time and the merchant
internal data class InboxCardText(
    val sourceLabel: String,
    val timeLabel: String,
    val merchant: String,
)

// FR-087, FR-091: a wallet or category of a card or pair side; id is null until the user or a suggestion picks one
internal data class InboxSlot(
    val id: Long?,
    val name: String?,
)

// FR-091, S-07: a debit and a credit that look like one transfer; from is the DEBIT side
internal data class InboxPairUi(
    val fromItemId: Long,
    val toItemId: Long,
    val from: InboxSlot,
    val to: InboxSlot,
    val amount: Money,
)

// S-07 "Selesai": recently confirmed or dismissed items, shown collapsed
internal data class InboxResolvedUi(
    val itemId: Long,
    val label: String,
    val amount: Money,
    val type: TransactionType,
    val isConfirmed: Boolean,
)

// FR-087: a user edit of a card before Catat; kept in the ViewModel until the item is resolved
internal data class InboxEdit(
    val walletId: Long? = null,
    val categoryId: Long? = null,
)

// S-07: the local, non-persisted state that changes what the list shows
internal data class InboxLocal(
    val edits: Map<Long, InboxEdit> = emptyMap(),
    val separated: Set<Long> = emptySet(),
    val undoItemId: Long? = null,
)
