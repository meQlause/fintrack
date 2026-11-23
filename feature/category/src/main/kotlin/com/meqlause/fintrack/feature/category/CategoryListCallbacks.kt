package com.meqlause.fintrack.feature.category

import com.meqlause.fintrack.core.model.Category

// FR-022, FR-023, S-10: the row actions of the category list; defaults do nothing
internal data class CategoryListCallbacks(
    val onEdit: (Category) -> Unit = {},
    val onMoveUp: (Category) -> Unit = {},
    val onMoveDown: (Category) -> Unit = {},
    val onSetArchived: (Category, Boolean) -> Unit = { _, _ -> },
    val onDelete: (Category) -> Unit = {},
)
