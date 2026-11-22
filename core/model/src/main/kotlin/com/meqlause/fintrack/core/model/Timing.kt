package com.meqlause.fintrack.core.model

import java.time.Instant

/** DOC-02: when a transaction happened (BR-009) and its audit stamps. */
public data class Timing(
    public val occurredAt: Instant,
    public val audit: Audit,
)
