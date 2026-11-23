package com.meqlause.fintrack.feature.category.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomSheet
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.icon.ColorSwatch
import com.meqlause.fintrack.core.designsystem.icon.IconChoice
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
    ChoiceGrid(keys = CategoryAppearance.iconKeys) { key ->
        IconChoice(
            iconKey = key,
            colorKey = draft.colorKey,
            selected = key == draft.iconKey,
            onSelect = { callbacks.editor.onIconChange(key) },
        )
    }
    Text(text = stringResource(R.string.category_editor_color), style = FintrackTheme.typography.labelLarge)
    ChoiceGrid(keys = CategoryAppearance.colorKeys) { key ->
        ColorSwatch(
            colorKey = key,
            selected = key == draft.colorKey,
            onSelect = { callbacks.editor.onColorChange(key) },
        )
    }
}

// FR-021: the heading says whether the draft creates a new category or edits a stored one
private fun titleRes(draft: CategoryEditor): Int =
    if (draft.categoryId == null) R.string.category_editor_new else R.string.category_editor_edit

// FR-021: the choices in rows of eight, each cell drawn by the caller
@Composable
private fun ChoiceGrid(
    keys: List<String>,
    cell: @Composable (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall)) {
        keys.chunked(GRID_COLUMNS).forEach { line ->
            Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall)) {
                line.forEach { key -> cell(key) }
            }
        }
    }
}

private const val GRID_COLUMNS = 8
