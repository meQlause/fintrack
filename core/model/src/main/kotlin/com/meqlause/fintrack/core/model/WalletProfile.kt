package com.meqlause.fintrack.core.model

/** DOC-02: how a wallet is named and listed. name is 1-30 chars, unique among non-archived wallets. */
public data class WalletProfile(
    public val name: String,
    public val appearance: Appearance,
    public val sortOrder: Int,
)
