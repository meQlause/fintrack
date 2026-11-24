package com.meqlause.fintrack.feature.transaction.shortcut

import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId

// FR-039: everything the "Pintasan baru" form can ask of its ViewModel
internal sealed interface ShortcutFormEvent {
    data class NameChanged(
        val name: String,
    ) : ShortcutFormEvent

    data class AmountChanged(
        val digits: String,
    ) : ShortcutFormEvent

    data class NoteChanged(
        val note: String,
    ) : ShortcutFormEvent

    data class TypeSelected(
        val type: TransactionType,
    ) : ShortcutFormEvent

    data class SheetOpened(
        val sheet: ShortcutSheet,
    ) : ShortcutFormEvent

    data object SheetDismissed : ShortcutFormEvent

    data class CategoryPicked(
        val categoryId: CategoryId,
    ) : ShortcutFormEvent

    data class WalletPicked(
        val walletId: WalletId,
    ) : ShortcutFormEvent

    data object SaveRequested : ShortcutFormEvent
}
