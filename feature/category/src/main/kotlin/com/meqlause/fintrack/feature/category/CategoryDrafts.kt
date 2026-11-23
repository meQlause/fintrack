package com.meqlause.fintrack.feature.category

import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import java.time.Instant

// FR-021, FR-023: maps the edit draft and the sort orders onto stored categories
internal object CategoryDrafts {
    fun nextSortOrder(existingCategories: List<Category>): Int =
        (existingCategories.maxOfOrNull { it.profile.sortOrder } ?: -1) + 1

    fun newCategory(
        draft: CategoryEditor,
        sortOrder: Int,
        currentTime: Instant,
    ): Category =
        Category(
            categoryId = CategoryId(0L),
            profile = toProfile(draft, sortOrder),
            state = CategoryState(isDefault = false, isArchived = false),
            audit = Audit(createdAt = currentTime, updatedAt = currentTime),
        )

    fun withDraft(
        category: Category,
        draft: CategoryEditor,
        currentTime: Instant,
    ): Category =
        category.copy(
            profile = toProfile(draft, category.profile.sortOrder),
            audit = category.audit.copy(updatedAt = currentTime),
        )

    fun withOrder(
        category: Category,
        sortOrder: Int,
    ): Category = category.copy(profile = category.profile.copy(sortOrder = sortOrder))

    private fun toProfile(
        draft: CategoryEditor,
        sortOrder: Int,
    ): CategoryProfile =
        CategoryProfile(
            name = draft.name.trim(),
            kind = draft.kind,
            appearance = Appearance(iconKey = draft.iconKey, colorKey = draft.colorKey),
            sortOrder = sortOrder,
        )
}
