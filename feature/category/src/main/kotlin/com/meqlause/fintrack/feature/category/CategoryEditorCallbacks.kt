package com.meqlause.fintrack.feature.category

// FR-021, S-10: the actions of the category edit form; defaults do nothing
internal data class CategoryEditorCallbacks(
    val onNameChange: (String) -> Unit = {},
    val onIconChange: (String) -> Unit = {},
    val onColorChange: (String) -> Unit = {},
    val onSave: () -> Unit = {},
    val onDismissEditor: () -> Unit = {},
)
