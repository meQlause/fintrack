package com.meqlause.fintrack.core.model

/** DOC-02: values a quick entry pre-fills into the add sheet; [title] is only its label, [note] fills the draft. */
public data class QuickTemplate(
    public val title: String,
    public val type: TransactionType,
    public val amount: Money,
    public val note: String = "",
)
