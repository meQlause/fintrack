package com.meqlause.fintrack.feature.budget

// FR-070, FR-074: the S-11 sheet, first choosing a target (Pick), then typing its limit (Amount)
internal sealed interface BudgetEditor {
    data object Pick : BudgetEditor

    data class Amount(
        val target: BudgetTarget,
        val digits: String,
    ) : BudgetEditor
}
