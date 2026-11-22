package com.meqlause.fintrack.core.model

/** DOC-02: typed primary key of a merchant rule, so ids of different entities cannot be mixed up. */
@JvmInline
public value class MerchantRuleId(
    public val value: Long,
)
