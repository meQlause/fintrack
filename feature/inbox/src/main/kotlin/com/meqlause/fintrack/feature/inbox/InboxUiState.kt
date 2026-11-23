package com.meqlause.fintrack.feature.inbox

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.picker.PickerOption

// FR-084, FR-087, S-07: the whole Kotak Masuk screen; isLoaded stays false until the first emission
internal data class InboxUiState(
    val isLoaded: Boolean = false,
    val hasAccess: Boolean = true,
    val cards: List<InboxCardUi> = emptyList(),
    val pairs: List<InboxPairUi> = emptyList(),
    val resolved: List<InboxResolvedUi> = emptyList(),
    val walletOptions: List<PickerOption> = emptyList(),
    val expenseOptions: List<PickerOption> = emptyList(),
    val incomeOptions: List<PickerOption> = emptyList(),
    val undoItemId: Long? = null,
)

// FR-087, S-07: one card of the list; walletId and categoryId are the values that Catat will store
internal data class InboxCardUi(
    val itemId: Long,
    val sourceLabel: String,
    val timeLabel: String,
    val amount: Money,
    val type: TransactionType,
    val merchant: String,
    val walletId: Long?,
    val walletName: String?,
    val categoryId: Long?,
    val categoryName: String?,
)

// FR-091, S-07: a debit and a credit that look like one transfer; from is the DEBIT side
internal data class InboxPairUi(
    val fromItemId: Long,
    val toItemId: Long,
    val fromWalletId: Long?,
    val fromWalletName: String?,
    val toWalletId: Long?,
    val toWalletName: String?,
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
