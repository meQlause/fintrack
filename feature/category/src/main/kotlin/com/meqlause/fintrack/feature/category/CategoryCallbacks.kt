package com.meqlause.fintrack.feature.category

import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryKind

// DOC-03: every user action of S-10, passed to the stateless screen as plain lambdas
internal data class CategoryCallbacks(
    val onBack: () -> Unit = {},
    val onSelectKind: (CategoryKind) -> Unit = {},
    val onAdd: () -> Unit = {},
    val onEdit: (Category) -> Unit = {},
    val onMoveUp: (Category) -> Unit = {},
    val onMoveDown: (Category) -> Unit = {},
    val onSetArchived: (Category, Boolean) -> Unit = { _, _ -> },
    val onDelete: (Category) -> Unit = {},
    val onNameChange: (String) -> Unit = {},
    val onIconChange: (String) -> Unit = {},
    val onColorChange: (String) -> Unit = {},
    val onSave: () -> Unit = {},
    val onDismissEditor: () -> Unit = {},
    val onArchiveOfferConfirm: () -> Unit = {},
    val onArchiveOfferDismiss: () -> Unit = {},
)
