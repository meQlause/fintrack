package com.meqlause.fintrack.core.model

/** DOC-02: the wallet, transaction type and history view the screens open with by default (S-05). */
public data class LastUsed(
    public val walletId: WalletId? = null,
    public val transactionType: TransactionType = TransactionType.EXPENSE,
    public val histView: HistoryView = HistoryView.LIST,
)
