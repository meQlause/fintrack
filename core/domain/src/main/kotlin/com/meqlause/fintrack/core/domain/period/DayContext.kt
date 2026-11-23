package com.meqlause.fintrack.core.domain.period

import java.time.LocalDate
import java.time.ZoneId

/** The local day and the zone that date-based rules are evaluated in, passed together. */
public data class DayContext(
    public val today: LocalDate,
    public val zone: ZoneId,
)
