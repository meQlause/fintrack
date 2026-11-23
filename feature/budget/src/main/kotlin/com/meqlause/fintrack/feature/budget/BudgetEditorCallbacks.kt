package com.meqlause.fintrack.feature.budget

// FR-072: the keypad, save and close actions of the budget amount editor; defaults do nothing
internal data class BudgetEditorCallbacks(
    val onDigit: (String) -> Unit = {},
    val onBackspace: () -> Unit = {},
    val onSave: () -> Unit = {},
    val onDismissEditor: () -> Unit = {},
)
