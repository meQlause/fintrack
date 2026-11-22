package com.meqlause.fintrack.core.model

/** DOC-02: a shortcut that pre-fills the add sheet (FR-039, BR-047). */
public data class QuickEntry(
    public val quickEntryId: QuickEntryId,
    public val template: QuickTemplate,
    public val categoryId: CategoryId,
    public val walletId: WalletId,
    public val sortOrder: Int,
    public val audit: Audit,
)
