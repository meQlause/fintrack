package com.meqlause.fintrack.core.domain.credit

import java.time.LocalDate
import java.time.YearMonth

/** BR-032: the statement for [month] with its cut-off, the first day it covers and its due date. */
public data class StatementPeriod(
    public val month: YearMonth,
    public val cutOff: LocalDate,
    public val periodStart: LocalDate,
    public val dueDate: LocalDate,
)
