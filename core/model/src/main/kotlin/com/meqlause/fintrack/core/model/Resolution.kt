package com.meqlause.fintrack.core.model

import java.time.Instant

/** DOC-02: review status of an inbox item and the transaction it produced (BR-011). */
public data class Resolution(
    public val status: InboxStatus,
    public val transactionId: TransactionId?,
    public val resolvedAt: Instant?,
)
