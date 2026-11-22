package com.meqlause.fintrack.core.model

import java.time.Instant

/** DOC-02: a manually entered current value of an INVESTMENT wallet (FR-172, ADR-017, BR-054). */
public data class ValuationEntry(
    public val valuationId: ValuationEntryId,
    public val walletId: WalletId,
    public val value: Money,
    public val valuedAt: Instant,
    public val createdAt: Instant,
)
