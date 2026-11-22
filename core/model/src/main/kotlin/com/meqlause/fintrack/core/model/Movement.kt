package com.meqlause.fintrack.core.model

/** DOC-02: what moved and how much; feeAmount is used by TRANSFER only (BR-004, BR-005). */
public data class Movement(
    public val type: TransactionType,
    public val amount: Money,
    public val feeAmount: Money = Money(0L),
)
