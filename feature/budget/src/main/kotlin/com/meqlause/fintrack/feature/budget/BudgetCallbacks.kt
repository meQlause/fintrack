package com.meqlause.fintrack.feature.budget

// DOC-03: every user action of S-11, wired by BudgetRoute and inert in previews
internal data class BudgetCallbacks(
    val onBack: () -> Unit = {},
    val onAdd: () -> Unit = {},
    val onEdit: (BudgetRow) -> Unit = {},
    val onPick: (BudgetTarget) -> Unit = {},
    val editor: BudgetEditorCallbacks = BudgetEditorCallbacks(),
)
