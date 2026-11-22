package com.meqlause.fintrack.core.model

import java.time.Instant

/** DOC-02: the notification as received; rawTitle and rawText are purged 30 days after resolution (FR-092). */
public data class RawNotice(
    public val packageName: String,
    public val parserId: String,
    public val rawTitle: String,
    public val rawText: String,
    public val postedAt: Instant,
    public val fingerprint: String,
)
