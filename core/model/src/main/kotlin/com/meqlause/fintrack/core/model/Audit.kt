package com.meqlause.fintrack.core.model

import java.time.Instant

/** DOC-02: audit fields every table carries; grouped to keep entity constructors within 7 parameters. */
public data class Audit(
    public val createdAt: Instant,
    public val updatedAt: Instant,
)
