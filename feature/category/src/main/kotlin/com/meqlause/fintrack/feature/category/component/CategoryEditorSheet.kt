package com.meqlause.fintrack.feature.category.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomSheet
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackChip
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.category.CategoryAppearance
import com.meqlause.fintrack.feature.category.CategoryCallbacks
import com.meqlause.fintrack.feature.category.CategoryEditor
import com.meqlause.fintrack.feature.category.R

// FR-021, S-10: the edit form: name, icon row and colour row; the kind is not editable (BR-006)
@Composable
internal fun CategoryEditorSheet(
    draft: CategoryEditor,
    callbacks: CategoryCallbacks,
    modifier: Modifier = Modifier,
) {
    FintrackBottomSheet(modifier = modifier) {
        Text(
            text = stringResource(titleRes(draft)),
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        CategoryEditorFields(draft = draft, callbacks = callbacks)
        Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
            FintrackButton(text = stringResource(R.string.category_editor_save), onClick = callbacks.editor.onSave)
            FintrackButton(
                text = stringResource(R.string.category_editor_cancel),
                onClick = callbacks.editor.onDismissEditor,
            )
        }
    }
}

// FR-021: the name field and the icon and colour choices of the draft
@Composable
private fun CategoryEditorFields(
    draft: CategoryEditor,
    callbacks: CategoryCallbacks,
) {
    TextField(
        value = draft.name,
        onValueChange = callbacks.editor.onNameChange,
        label = { Text(text = stringResource(R.string.category_editor_name)) },
        singleLine = true,
    )
    Text(text = stringResource(R.string.category_editor_icon), style = FintrackTheme.typography.labelLarge)
    ChoiceRow(
        keys = CategoryAppearance.iconKeys,
        selected = draft.iconKey,
        labelRes = R.string.category_icon_option,
        onSelect = callbacks.editor.onIconChange,
    )
    Text(text = stringResource(R.string.category_editor_color), style = FintrackTheme.typography.labelLarge)
    ChoiceRow(
        keys = CategoryAppearance.colorKeys,
        selected = draft.colorKey,
        labelRes = R.string.category_color_option,
        onSelect = callbacks.editor.onColorChange,
    )
}

// FR-021: the heading says whether the draft creates a new category or edits a stored one
private fun titleRes(draft: CategoryEditor): Int =
    if (draft.categoryId == null) R.string.category_editor_new else R.string.category_editor_edit

// FR-021: a wrapping-free row of chips; each chip is labelled by its position in the catalogue
@Composable
private fun ChoiceRow(
    keys: List<String>,
    selected: String,
    labelRes: Int,
    onSelect: (String) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall)) {
        keys.forEachIndexed { index, key ->
            FintrackChip(
                label = stringResource(labelRes, index + 1),
                selected = key == selected,
                onClick = { onSelect(key) },
            )
        }
    }
}
