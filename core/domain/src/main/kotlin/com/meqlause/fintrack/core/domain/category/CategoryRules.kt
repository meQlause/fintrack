package com.meqlause.fintrack.core.domain.category

import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryProfile

private const val MAX_NAME_LENGTH = 24

// FR-021, DOC-02: a name is 1 to 24 trimmed characters, unique per kind among non-archived categories
internal object CategoryRules {
    fun check(
        profile: CategoryProfile,
        existing: List<Category>,
    ): Set<CategoryError> =
        buildSet {
            val name = profile.name.trim()
            if (name.isEmpty()) add(CategoryError.NAME_REQUIRED)
            if (name.length > MAX_NAME_LENGTH) add(CategoryError.NAME_TOO_LONG)
            val taken =
                existing.any {
                    !it.state.isArchived &&
                        it.profile.kind == profile.kind &&
                        it.profile.name
                            .trim()
                            .equals(name, ignoreCase = true)
                }
            if (name.isNotEmpty() && taken) add(CategoryError.NAME_TAKEN)
        }
}
