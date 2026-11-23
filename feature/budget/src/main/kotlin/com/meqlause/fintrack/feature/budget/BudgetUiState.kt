package com.meqlause.fintrack.feature.budget

import com.meqlause.fintrack.core.model.Category

// FR-070..072, S-11: everything S-11 shows; loaded is false until the first lists arrive
internal data class BudgetUiState(
    val loaded: Boolean = false,
    val overall: BudgetRow? = null,
    val rows: List<BudgetRow> = emptyList(),
    val categories: List<Category> = emptyList(),
    val editor: BudgetEditor? = null,
)
