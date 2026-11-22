package com.meqlause.fintrack.core.domain.period

import java.time.LocalDate

/** BR-010, FR-050: one budget/statistics period. [start] and [end] are both inclusive. */
public data class Period(
    public val start: LocalDate,
    public val end: LocalDate,
)
