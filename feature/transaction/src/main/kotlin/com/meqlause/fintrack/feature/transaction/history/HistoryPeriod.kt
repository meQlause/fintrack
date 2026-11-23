package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.domain.period.PeriodCalculator
import java.time.YearMonth

// BR-010, FR-036: the month the user picked runs from its start day, so a start day of 25 makes "Nov" 25 Nov to 24 Dec
internal fun monthPeriod(
    month: YearMonth,
    startDay: Int,
): Period = PeriodCalculator.periodContaining(month.atDay(startDay), startDay)
