package com.meqlause.fintrack.core.model

/** DOC-02: what the parser extracted from the notification; merchant may be empty. */
public data class ParsedAmount(
    public val amount: Money,
    public val direction: MoneyDirection,
    public val merchant: String,
)
