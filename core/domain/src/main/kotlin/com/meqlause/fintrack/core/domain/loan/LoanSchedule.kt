package com.meqlause.fintrack.core.domain.loan

import java.time.Instant
import java.time.LocalDate

/** FR-150, FR-158: when a loan starts and when it is due; a null dueDate means no due date. */
public data class LoanSchedule(
    public val startDate: Instant,
    public val dueDate: LocalDate?,
)
