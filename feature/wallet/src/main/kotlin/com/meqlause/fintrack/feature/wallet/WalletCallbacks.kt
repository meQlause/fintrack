package com.meqlause.fintrack.feature.wallet

import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType

// FR-010..016: every user action of S-09, passed to the stateless screen as plain lambdas
internal data class WalletCallbacks(
    val list: ListLinks = ListLinks(),
    val fields: Fields = Fields(),
    val picks: Picks = Picks(),
    val sheet: Sheet = Sheet(),
) {
    val onAdd: () -> Unit get() = list.onAdd
    val onOpenDetail: (WalletId, WalletType) -> Unit get() = list.onOpenDetail
    val onOpenLoans: () -> Unit get() = list.onOpenLoans
    val onEdit: (WalletItem) -> Unit get() = list.onEdit
    val onToggleArchived: () -> Unit get() = list.onToggleArchived
    val onNameChange: (String) -> Unit get() = fields.onNameChange
    val onInitialBalanceChange: (String) -> Unit get() = fields.onInitialBalanceChange
    val onCreditLimitChange: (String) -> Unit get() = fields.onCreditLimitChange
    val onIncludeInTotalChange: (Boolean) -> Unit get() = fields.onIncludeInTotalChange
    val onOpenPicker: (WalletPicker) -> Unit get() = picks.onOpenPicker
    val onDismissPicker: () -> Unit get() = picks.onDismissPicker
    val onPickType: (WalletType) -> Unit get() = picks.onPickType
    val onPickInvestKind: (InvestKind) -> Unit get() = picks.onPickInvestKind
    val onPickDay: (Int) -> Unit get() = picks.onPickDay
    val onSetArchivedEditing: (Boolean) -> Unit get() = sheet.onSetArchivedEditing
    val onSave: () -> Unit get() = sheet.onSave
    val onDismissEditor: () -> Unit get() = sheet.onDismissEditor

    // S-09: the list, its add button, the loans row and the archived section
    internal data class ListLinks(
        val onAdd: () -> Unit = {},
        val onOpenDetail: (WalletId, WalletType) -> Unit = { _, _ -> },
        val onOpenLoans: () -> Unit = {},
        val onEdit: (WalletItem) -> Unit = {},
        val onToggleArchived: () -> Unit = {},
    )

    // FR-010, FR-139: the text and switch fields of the editor
    internal data class Fields(
        val onNameChange: (String) -> Unit = {},
        val onInitialBalanceChange: (String) -> Unit = {},
        val onCreditLimitChange: (String) -> Unit = {},
        val onIncludeInTotalChange: (Boolean) -> Unit = {},
    )

    // FR-010, FR-139, FR-170: the drawers the editor opens and what they pick
    internal data class Picks(
        val onOpenPicker: (WalletPicker) -> Unit = {},
        val onDismissPicker: () -> Unit = {},
        val onPickType: (WalletType) -> Unit = {},
        val onPickInvestKind: (InvestKind) -> Unit = {},
        val onPickDay: (Int) -> Unit = {},
    )

    // FR-010, FR-012: saving, archiving and closing the editor
    internal data class Sheet(
        val onSetArchivedEditing: (Boolean) -> Unit = {},
        val onSave: () -> Unit = {},
        val onDismissEditor: () -> Unit = {},
    )
}
