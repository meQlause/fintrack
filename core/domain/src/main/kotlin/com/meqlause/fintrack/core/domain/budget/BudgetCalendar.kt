package com.meqlause.fintrack.core.domain.budget

import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.domain.period.PeriodCalculator
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** BR-010, FR-073: the budget period of today and the instant that keys it in the alert log. */
public object BudgetCalendar {
    /** BR-010: the period containing today in the zone of [clock], for periods starting on [startDay]. */
    public fun currentPeriod(
        clock: Clock,
        startDay: Int,
    ): Period = PeriodCalculator.periodContaining(LocalDate.now(clock), startDay)

    /** FR-073: the first moment of [period] in [zone], stored as periodStart in BudgetAlertLog. */
    public fun startInstant(
        period: Period,
        zone: ZoneId,
    ): Instant = period.startDate.atStartOfDay(zone).toInstant()
}
