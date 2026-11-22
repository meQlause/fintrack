package com.meqlause.fintrack.core.domain.credit

import java.time.LocalDate
import java.time.YearMonth

/** BR-032, BR-034, BR-036: statement cut-off, period and due-date rules. Pure; callers pass dates. */
public object StatementDates {
    private const val FIRST_DAY = 1
    private const val LAST_DAY = 28

    /** BR-032: the cut-off is the statement day of [month]; statement days are 1..28 (FR-130). */
    public fun cutOff(
        month: YearMonth,
        statementDay: Int,
    ): LocalDate = month.atDay(checkedDay(statementDay))

    /** BR-032: due day in the cut-off month when it is later than the statement day, otherwise next month. */
    public fun dueDate(
        month: YearMonth,
        statementDay: Int,
        dueDay: Int,
    ): LocalDate {
        val dueMonth = if (dueDay > checkedDay(statementDay)) month else month.plusMonths(1)
        return dueMonth.atDay(checkedDay(dueDay))
    }

    /** BR-032: the period covers the day after the previous cut-off up to this cut-off. */
    public fun periodStart(
        month: YearMonth,
        statementDay: Int,
    ): LocalDate = cutOff(month.minusMonths(1), statementDay).plusDays(1)

    /** BR-036: the first statement whose cut-off is on or after [date] (wallet creation or purchase). */
    public fun firstStatementMonth(
        date: LocalDate,
        statementDay: Int,
    ): YearMonth {
        val month = YearMonth.from(date)
        return if (cutOff(month, statementDay).isBefore(date)) month.plusMonths(1) else month
    }

    /** BR-034: the latest statement month whose cut-off is on or before [today]. */
    public fun latestCutOffMonth(
        today: LocalDate,
        statementDay: Int,
    ): YearMonth {
        val month = YearMonth.from(today)
        return if (cutOff(month, statementDay).isAfter(today)) month.minusMonths(1) else month
    }

    private fun checkedDay(value: Int): Int {
        require(value in FIRST_DAY..LAST_DAY) { "day must be 1..28 (FR-130)" }
        return value
    }
}
