package com.meqlause.fintrack.core.model

/** DOC-02: where a transaction came from; inboxItemId is set when it was created from the inbox. */
public data class Origin(
    public val source: TransactionSource,
    public val inboxItemId: InboxItemId?,
)
