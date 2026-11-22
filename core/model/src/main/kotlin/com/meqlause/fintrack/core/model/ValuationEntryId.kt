package com.meqlause.fintrack.core.model

/** DOC-02: typed primary key of a valuation entry, so ids of different entities cannot be mixed up. */
@JvmInline
public value class ValuationEntryId(
    public val value: Long,
)
