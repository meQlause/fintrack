package com.meqlause.fintrack.core.model

/** DOC-02: typed primary key of a budget, so ids of different entities cannot be mixed up. */
@JvmInline
public value class BudgetId(
    public val value: Long,
)
