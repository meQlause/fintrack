package com.meqlause.fintrack.core.model

/** DOC-02: the wallet and transaction type the add sheet opens with by default. */
public data class LastUsed(
    public val walletId: WalletId? = null,
    public val transactionType: TransactionType = TransactionType.EXPENSE,
)
