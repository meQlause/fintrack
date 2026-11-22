package com.meqlause.fintrack.core.model

/** DOC-02: terms of a credit wallet only; creditLimit > 0, days are 1-28 (BR-032, BR-044). */
public data class CreditTerms(
    public val creditLimit: Money,
    public val statementDay: Int,
    public val dueDay: Int,
)
