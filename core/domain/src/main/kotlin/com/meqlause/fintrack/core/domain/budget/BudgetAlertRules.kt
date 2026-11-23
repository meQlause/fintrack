package com.meqlause.fintrack.core.domain.budget

/** BR-021, FR-073: which thresholds a budget has crossed and not yet alerted for in this period. */
public object BudgetAlertRules {
    /** BR-021: every threshold at or below [percent] that is not in [alreadySent], ascending. */
    public fun unsentCrossings(
        percent: Long,
        alreadySent: Set<Int>,
    ): List<Int> = THRESHOLDS.filter { percent >= it && it !in alreadySent }

    private val THRESHOLDS: List<Int> =
        listOf(BudgetRules.WARNING_PERCENT.toInt(), BudgetRules.EXCEEDED_PERCENT.toInt())
}
