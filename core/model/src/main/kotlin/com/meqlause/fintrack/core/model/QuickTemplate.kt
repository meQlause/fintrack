package com.meqlause.fintrack.core.model

/** DOC-02: values a quick entry pre-fills into the add sheet; never saved by itself (BR-047). */
public data class QuickTemplate(
    public val title: String,
    public val type: TransactionType,
    public val amount: Money,
)
