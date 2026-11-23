package com.meqlause.fintrack.core.domain.reminder

// BR-045, BR-055: one notification the daily reminder job may post
public sealed interface Reminder {
    /** BR-045: stable per item, due date and lead time, so the same reminder maps to the same notification. */
    public val key: String
}
