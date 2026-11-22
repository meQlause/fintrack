package com.meqlause.fintrack.core.domain.usecase

/**
 * DOC-02, FR-004: the display names of the seeded categories. The platform supplies them from string
 * resources, because core:domain is pure Kotlin and cannot read Android resources.
 */
public interface DefaultCategoryNames {
    /** FR-004: the Indonesian display name of [category], stored as plain data once seeded. */
    public fun nameOf(category: DefaultCategory): String
}
