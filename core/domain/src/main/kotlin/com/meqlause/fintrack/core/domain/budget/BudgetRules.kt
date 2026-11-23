package com.meqlause.fintrack.core.domain.budget

import com.meqlause.fintrack.core.model.BudgetState
import com.meqlause.fintrack.core.model.Money

/** BR-020, FR-072: budget usage percent and band, computed with integer math only. */
public object BudgetRules {
    /** BR-020: the warning band starts at 80 percent. */
    public const val WARNING_PERCENT: Long = 80L

    /** BR-020: the exceeded band starts at 100 percent. */
    public const val EXCEEDED_PERCENT: Long = 100L

    private const val PERCENT_SCALE: Long = 100L

    /** BR-020: spent * 100 / limit, rounded down; a zero or negative limit yields 0. */
    public fun percentOf(
        spent: Money,
        limit: Money,
    ): Long = if (limit.rupiah <= 0L) 0L else spent.rupiah * PERCENT_SCALE / limit.rupiah

    /** BR-020: NORMAL below 80 percent, WARNING from 80 to 99 percent, EXCEEDED from 100 percent. */
    public fun stateOf(percent: Long): BudgetState =
        when {
            percent >= EXCEEDED_PERCENT -> BudgetState.EXCEEDED
            percent >= WARNING_PERCENT -> BudgetState.WARNING
            else -> BudgetState.NORMAL
        }
}
