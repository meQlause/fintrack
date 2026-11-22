package com.meqlause.fintrack.core.domain.period

import java.time.LocalDate

/** BR-010, FR-050: one budget/statistics period. [startDate] and [endDate] are both inclusive. */
public data class Period(
    public val startDate: LocalDate,
    public val endDate: LocalDate,
)
