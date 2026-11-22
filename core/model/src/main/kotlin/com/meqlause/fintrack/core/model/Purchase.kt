package com.meqlause.fintrack.core.model

/** DOC-02: the credit purchase an instalment plan splits; principal equals its amount (ADR-015, BR-038). */
public data class Purchase(
    public val transactionId: TransactionId,
    public val walletId: WalletId,
    public val title: String,
    public val principal: Money,
    public val adminFeeTransactionId: TransactionId?,
)
