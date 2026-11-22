package com.meqlause.fintrack.core.model

/** DOC-02: how a category is named, classified and listed; kind is immutable after creation (BR-006). */
public data class CategoryProfile(
    public val name: String,
    public val kind: CategoryKind,
    public val appearance: Appearance,
    public val sortOrder: Int,
)
