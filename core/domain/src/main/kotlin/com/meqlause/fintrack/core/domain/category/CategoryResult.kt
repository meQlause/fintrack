package com.meqlause.fintrack.core.domain.category

import com.meqlause.fintrack.core.model.CategoryId

/** FR-021: outcome of creating a category; a rejected category stores nothing. */
public sealed interface CategoryResult {
    /** FR-021: the category is stored under [categoryId]. */
    public data class Created(
        public val categoryId: CategoryId,
    ) : CategoryResult

    /** FR-021: every rule the category breaks. */
    public data class Rejected(
        public val errors: Set<CategoryError>,
    ) : CategoryResult
}
