package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.model.Money
import java.time.LocalDate

// FR-041, BR-010: income, expense and net of the current period; both dates are inclusive
internal data class HomePeriod(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val income: Money,
    val expense: Money,
) {
    val netTotal: Money
        get() = income - expense
}
