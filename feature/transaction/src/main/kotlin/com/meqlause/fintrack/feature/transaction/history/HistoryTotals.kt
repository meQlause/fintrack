package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.model.Money

// FR-036: income and expense totals of the shown transactions; transfers and loans are not counted
internal data class HistoryTotals(
    val income: Money = Money(0L),
    val expense: Money = Money(0L),
) {
    val netTotal: Money
        get() = income - expense
}
