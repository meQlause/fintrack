package com.meqlause.fintrack.core.model

/** DOC-02: typed primary key of a loan, so ids of different entities cannot be mixed up. */
@JvmInline
public value class LoanId(
    public val value: Long,
)
