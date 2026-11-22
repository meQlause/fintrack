package com.meqlause.fintrack.core.model

/** DOC-02: typed primary key of a quick entry, so ids of different entities cannot be mixed up. */
@JvmInline
public value class QuickEntryId(
    public val value: Long,
)
