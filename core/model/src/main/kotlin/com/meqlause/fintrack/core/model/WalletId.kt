package com.meqlause.fintrack.core.model

/** DOC-02: typed primary key of a wallet, so ids of different entities cannot be mixed up. */
@JvmInline
public value class WalletId(
    public val value: Long,
)
