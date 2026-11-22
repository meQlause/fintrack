package com.meqlause.fintrack.core.domain.period

import java.time.LocalDate
import java.time.YearMonth

/** BR-010, FR-050: pure period math. Callers pass the date; this object never reads the clock. */
public object PeriodCalculator {
    private const val MIN_START_DAY = 1
    private const val MAX_START_DAY = 28

    /**
     * BR-010: the period that contains [date] when periods begin on [startDay].
     * A date on or after [startDay] starts a period in its own month; an earlier date belongs to the
     * period that began on [startDay] of the previous month. Start days are 1..28 (FR-050), so every
     * month has that day and no clamping is needed.
     */
    public fun periodContaining(
        date: LocalDate,
        startDay: Int,
    ): Period {
        require(startDay in MIN_START_DAY..MAX_START_DAY) { "start day must be 1..28 (FR-050)" }
        val month = YearMonth.from(date)
        val startMonth = if (date.dayOfMonth >= startDay) month else month.minusMonths(1)
        val start = startMonth.atDay(startDay)
        return Period(start, start.plusMonths(1).minusDays(1))
    }

    /** BR-010: the period that follows [period]; it starts the day after [period] ends. */
    public fun nextPeriod(
        period: Period,
        startDay: Int,
    ): Period = periodContaining(period.endDate.plusDays(1), startDay)

    /** BR-010, FR-062: the period that precedes [period], used for comparison with the current one. */
    public fun previousPeriod(
        period: Period,
        startDay: Int,
    ): Period = periodContaining(period.startDate.minusDays(1), startDay)
}
