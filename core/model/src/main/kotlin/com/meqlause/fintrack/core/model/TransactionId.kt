package com.meqlause.fintrack.core.model

/** DOC-02: typed primary key of a transaction, so ids of different entities cannot be mixed up. */
@JvmInline
public value class TransactionId(
    public val value: Long,
)
