package com.meqlause.fintrack.core.model

/** DOC-02: opening balance and, for credit wallets only, the credit terms (BR-031, BR-032). */
public data class WalletTerms(
    public val initialBalance: Money,
    public val credit: CreditTerms?,
)
