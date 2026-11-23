package com.meqlause.fintrack.feature.budget

import com.meqlause.fintrack.core.domain.budget.BudgetStatus

// FR-071: one budget line, paired with the target it edits
internal data class BudgetRow(
    val target: BudgetTarget,
    val status: BudgetStatus,
)
