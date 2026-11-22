package com.meqlause.fintrack.feature.category

import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind

// FR-021: the draft of the name, icon and colour edit form; the kind is fixed once created (BR-006)
internal data class CategoryEditor(
    val categoryId: CategoryId?,
    val kind: CategoryKind,
    val name: String,
    val iconKey: String,
    val colorKey: String,
) {
    companion object {
        // FR-021: a blank draft for a new category of the selected kind
        fun forNew(kind: CategoryKind): CategoryEditor =
            CategoryEditor(
                categoryId = null,
                kind = kind,
                name = "",
                iconKey = CategoryAppearance.DEFAULT_ICON,
                colorKey = CategoryAppearance.DEFAULT_COLOR,
            )

        // FR-021: a draft that starts from the stored name, icon and colour of [category]
        fun of(category: Category): CategoryEditor =
            CategoryEditor(
                categoryId = category.categoryId,
                kind = category.profile.kind,
                name = category.profile.name,
                iconKey = category.profile.appearance.iconKey,
                colorKey = category.profile.appearance.colorKey,
            )
    }
}
