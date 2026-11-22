package com.meqlause.fintrack.core.model

/** DOC-02: a category for expenses or income (FR-004). */
public data class Category(
    public val categoryId: CategoryId,
    public val profile: CategoryProfile,
    public val state: CategoryState,
    public val audit: Audit,
)
