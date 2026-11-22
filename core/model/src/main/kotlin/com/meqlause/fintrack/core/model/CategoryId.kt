package com.meqlause.fintrack.core.model

/** DOC-02: typed primary key of a category, so ids of different entities cannot be mixed up. */
@JvmInline
public value class CategoryId(
    public val value: Long,
)
