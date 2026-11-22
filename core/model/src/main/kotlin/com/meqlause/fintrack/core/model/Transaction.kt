package com.meqlause.fintrack.core.model

/** DOC-02: one money movement (BR-001, BR-002). Indexed by occurredAt and wallet. */
public data class Transaction(
    public val transactionId: TransactionId,
    public val movement: Movement,
    public val routing: Routing,
    public val memo: Memo,
    public val origin: Origin,
    public val timing: Timing,
)
