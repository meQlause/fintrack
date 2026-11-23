package com.meqlause.fintrack.core.model

import java.time.Instant

/** FR-083, DOC-07: a notification as received from the listener, before any parsing. */
public data class RawNotification(
    public val packageName: String,
    public val title: String,
    public val text: String,
    public val bigText: String,
    public val postedAt: Instant,
    public val notificationKey: String,
)
