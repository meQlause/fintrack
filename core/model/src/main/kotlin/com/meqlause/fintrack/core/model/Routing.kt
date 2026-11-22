package com.meqlause.fintrack.core.model

/** DOC-02: wallets, category and loan a transaction is attached to (BR-002, BR-003). */
public data class Routing(
    public val walletId: WalletId,
    public val targetWalletId: WalletId?,
    public val categoryId: CategoryId?,
    public val loanId: LoanId?,
)
