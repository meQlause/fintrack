package com.meqlause.fintrack.core.model

/** DOC-02: a transaction detected from a notification, waiting for review (FR-091, BR-011). */
public data class InboxItem(
    public val inboxItemId: InboxItemId,
    public val notice: RawNotice,
    public val parsed: ParsedAmount,
    public val suggestion: Suggestion,
    public val resolution: Resolution,
    public val audit: Audit,
)
