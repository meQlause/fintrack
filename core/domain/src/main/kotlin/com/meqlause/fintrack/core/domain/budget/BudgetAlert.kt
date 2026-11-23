package com.meqlause.fintrack.core.domain.budget

import com.meqlause.fintrack.core.model.BudgetId
import com.meqlause.fintrack.core.model.Money

/** FR-073: the content of one budget alert; categoryName is null for the overall budget (FR-074). */
public data class BudgetAlert(
    public val budgetId: BudgetId,
    public val categoryName: String?,
    public val threshold: Int,
    public val spent: Money,
    public val limit: Money,
)
