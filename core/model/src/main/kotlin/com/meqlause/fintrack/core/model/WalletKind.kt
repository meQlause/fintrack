package com.meqlause.fintrack.core.model

/** DOC-02: what kind of wallet it is; investKind applies to INVESTMENT wallets only. */
public data class WalletKind(
    public val type: WalletType,
    public val investKind: InvestKind?,
)
