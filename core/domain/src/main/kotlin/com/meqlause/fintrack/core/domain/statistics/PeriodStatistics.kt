package com.meqlause.fintrack.core.domain.statistics

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType

/** FR-060..062: one kind of money over a period, ranked by category, with per-day sums and the previous period. */
public data class PeriodStatistics(
    public val type: TransactionType,
    public val total: Money,
    public val previousTotal: Money,
    public val categories: List<CategoryChange>,
    public val dailyTotals: List<Money>,
) {
    /** FR-062: change of the total against the previous period in whole percent; null without a previous total. */
    public val totalPercentChange: Long?
        get() = percentChangeOf(total, previousTotal)

    /** FR-060: the category's share of the total in whole percent; zero when the total is zero. */
    public fun shareOf(change: CategoryChange): Long =
        if (total.isZero) 0L else Math.round(change.amount.rupiah * WHOLE_PERCENT / total.rupiah)
}

private const val WHOLE_PERCENT = 100.0
