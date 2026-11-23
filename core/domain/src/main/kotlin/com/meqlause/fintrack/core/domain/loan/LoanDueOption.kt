package com.meqlause.fintrack.core.domain.loan

import java.time.LocalDate

/** FR-158: the due-date choices offered when a loan is recorded or its due date is changed later */
public enum class LoanDueOption {
    NONE,
    ONE_WEEK,
    TWO_WEEKS,
    ONE_MONTH,
    TWO_MONTHS,
    THREE_MONTHS,
    ;

    /** FR-158: the due date counted from [start]; null for [NONE]. */
    public fun dueFrom(start: LocalDate): LocalDate? =
        when (this) {
            NONE -> null
            ONE_WEEK -> start.plusWeeks(1L)
            TWO_WEEKS -> start.plusWeeks(2L)
            ONE_MONTH -> start.plusMonths(1L)
            TWO_MONTHS -> start.plusMonths(2L)
            THREE_MONTHS -> start.plusMonths(QUARTER_MONTHS)
        }
}

private const val QUARTER_MONTHS = 3L
