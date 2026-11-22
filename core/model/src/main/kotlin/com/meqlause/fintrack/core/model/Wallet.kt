package com.meqlause.fintrack.core.model

/** DOC-02: a wallet the user owns (FR-010). Balance is computed, never stored (BR-043). */
public data class Wallet(
    public val walletId: WalletId,
    public val kind: WalletKind,
    public val terms: WalletTerms,
    public val profile: WalletProfile,
    public val status: WalletStatus,
    public val audit: Audit,
)
