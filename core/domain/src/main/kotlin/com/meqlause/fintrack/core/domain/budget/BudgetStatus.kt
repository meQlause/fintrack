package com.meqlause.fintrack.core.domain.budget

import com.meqlause.fintrack.core.model.Budget
import com.meqlause.fintrack.core.model.BudgetState
import com.meqlause.fintrack.core.model.Money

/** FR-071, FR-072: one budget with its spending in the current period, ready for display. */
public data class BudgetStatus(
    public val budget: Budget,
    public val spent: Money,
) {
    /** BR-020: spent as a whole percent of the limit. */
    public val percent: Long
        get() = BudgetRules.percentOf(spent, budget.limitAmount)

    /** BR-020: the colour band of this budget. */
    public val state: BudgetState
        get() = BudgetRules.stateOf(percent)
}
