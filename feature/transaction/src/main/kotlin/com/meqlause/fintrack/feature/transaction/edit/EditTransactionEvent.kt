package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId

// S-04: everything the sheet can ask its ViewModel to do; the screen keeps no state of its own
internal sealed interface EditTransactionEvent {
    data class DigitTyped(
        val digit: String,
    ) : EditTransactionEvent

    data object BackspacePressed : EditTransactionEvent

    data class TypeSelected(
        val type: TransactionType,
    ) : EditTransactionEvent

    data class FieldFocused(
        val field: KeypadField,
    ) : EditTransactionEvent

    data class SheetOpened(
        val sheet: EditSheet,
    ) : EditTransactionEvent

    data object SheetDismissed : EditTransactionEvent

    data class WalletPicked(
        val walletId: WalletId,
    ) : EditTransactionEvent

    data class CategoryPicked(
        val categoryId: CategoryId,
    ) : EditTransactionEvent

    data class CategoryEdited(
        val change: CategoryChange,
    ) : EditTransactionEvent

    data object WalletsSwapped : EditTransactionEvent

    data class NoteChanged(
        val note: String,
    ) : EditTransactionEvent

    data object SaveRequested : EditTransactionEvent

    data class ShortcutPicked(
        val quickEntryId: QuickEntryId,
    ) : EditTransactionEvent

    data class ShortcutDeleted(
        val quickEntryId: QuickEntryId,
    ) : EditTransactionEvent

    data object ShortcutRestored : EditTransactionEvent

    data class ShortcutNameChanged(
        val name: String,
    ) : EditTransactionEvent

    data class ShortcutToggled(
        val keep: Boolean,
    ) : EditTransactionEvent

    data class InstalmentToggled(
        val enabled: Boolean,
    ) : EditTransactionEvent

    data class InstalmentTenorPicked(
        val months: Int?,
    ) : EditTransactionEvent

    data class LoanEdited(
        val change: LoanChange,
    ) : EditTransactionEvent
}
