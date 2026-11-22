package com.meqlause.fintrack.core.model

import java.time.Instant

/** DOC-02: the terms of a loan agreed at opening; direction is immutable (ADR-016, FR-150). */
public data class Agreement(
    public val direction: LoanDirection,
    public val personName: String,
    public val principal: Money,
    public val startDate: Instant,
    public val dueDate: Instant?,
    public val note: String,
)
