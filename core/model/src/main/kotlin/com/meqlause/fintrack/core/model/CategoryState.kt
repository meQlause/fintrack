package com.meqlause.fintrack.core.model

/** DOC-02: seeded categories are default but still editable; archived ones stay valid for history (BR-007). */
public data class CategoryState(
    public val isDefault: Boolean,
    public val isArchived: Boolean,
)
