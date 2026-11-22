package com.meqlause.fintrack.core.model

import java.time.YearMonth

/** DOC-02: instalment tenor and statement months; portions are computed, never stored (BR-035, BR-037). */
public data class Schedule(
    public val tenorMonths: Int,
    public val firstStatementMonth: YearMonth,
    public val earlyPayoffMonth: YearMonth?,
)
