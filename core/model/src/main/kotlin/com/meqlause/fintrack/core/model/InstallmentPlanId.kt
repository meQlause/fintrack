package com.meqlause.fintrack.core.model

/** DOC-02: typed primary key of an instalment plan, so ids of different entities cannot be mixed up. */
@JvmInline
public value class InstallmentPlanId(
    public val value: Long,
)
