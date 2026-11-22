package com.meqlause.fintrack.core.model

/** DOC-02: typed primary key of an inbox item, so ids of different entities cannot be mixed up. */
@JvmInline
public value class InboxItemId(
    public val value: Long,
)
