package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.designsystem.component.ScreenDashedButton
import com.meqlause.fintrack.core.designsystem.component.SheetFieldLabel
import com.meqlause.fintrack.core.designsystem.component.SheetTextField
import com.meqlause.fintrack.core.designsystem.icon.ChoiceGrid
import com.meqlause.fintrack.core.designsystem.icon.ColorSwatch
import com.meqlause.fintrack.core.designsystem.icon.FintrackIcons
import com.meqlause.fintrack.core.designsystem.icon.IconChoice
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.category.DefaultCategory
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.ui.picker.OptionChoices
import com.meqlause.fintrack.core.ui.picker.OptionsCard
import com.meqlause.fintrack.feature.transaction.R

// S-04: seven cells of 38 dp fit the drawer width at the mockup size
private const val GRID_COLUMNS = 7

private val COLOR_KEYS: List<String> = DefaultCategory.entries.map { it.colorKey }.distinct()

// FR-021, FR-030, FR-031, S-04: the category list of the open type, or the form that adds one to it
@Composable
internal fun EditCategoryDrawer(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val form = state.newCategory
    if (form == null) {
        EditCategoryList(state = state, onEvent = onEvent, modifier = modifier)
    } else {
        EditCategoryForm(form = form, onEvent = onEvent, modifier = modifier)
    }
}

// S-04, DOC-08: the standard option card with the dashed "Tambah kategori" button under it
@Composable
private fun EditCategoryList(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier,
) {
    val picked =
        state.form.categoryId
            ?.value
            ?.toString()
    PickerDrawer(
        title = stringResource(R.string.transaction_category_title),
        onClose = { onEvent(EditTransactionEvent.SheetDismissed) },
        modifier = modifier,
    ) {
        OptionsCard(
            choices = OptionChoices(options = state.categoryChoices.map { it.toOption() }, selectedKey = picked),
            onPick = { onEvent(EditTransactionEvent.CategoryPicked(CategoryId(it.optionKey.toLong()))) },
        )
        ScreenDashedButton(
            text = stringResource(R.string.transaction_category_add),
            onClick = { onEvent(EditTransactionEvent.CategoryEdited(CategoryChange.AddStarted)) },
            modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
        )
    }
}

// FR-021, S-04: name, icon and colour of the new category, then Simpan
@Composable
private fun EditCategoryForm(
    form: CategoryForm,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier,
) {
    val change = { edit: CategoryChange -> onEvent(EditTransactionEvent.CategoryEdited(edit)) }
    PickerDrawer(
        title = stringResource(R.string.transaction_category_new),
        onClose = { onEvent(EditTransactionEvent.SheetDismissed) },
        modifier = modifier,
    ) {
        CategoryNameField(form = form, change = change)
        CategoryLookChoices(form = form, change = change)
        FintrackButton(
            text = stringResource(R.string.transaction_save),
            onClick = { change(CategoryChange.SaveRequested) },
            modifier = Modifier.padding(top = FintrackScreenSizes.sheetTop),
        )
    }
}

// FR-021: the "Nama" input with the message of the first broken rule under it
@Composable
private fun CategoryNameField(
    form: CategoryForm,
    change: (CategoryChange) -> Unit,
) {
    SheetFieldLabel(text = stringResource(R.string.transaction_category_name))
    SheetTextField(
        value = form.name,
        onValueChange = { change(CategoryChange.NameChanged(it)) },
        placeholder = stringResource(R.string.transaction_category_name_hint),
    )
    form.errors.minByOrNull { it.ordinal }?.let { error ->
        Text(
            text = stringResource(error.messageRes()),
            style = FintrackScreenText.style(size = 12f, weight = 600),
            color = FintrackTheme.colors.semantics.error,
            modifier = Modifier.padding(top = FintrackTheme.spacing.xSmall),
        )
    }
}

// FR-021: the icon grid and the colour swatches of the new category
@Composable
private fun CategoryLookChoices(
    form: CategoryForm,
    change: (CategoryChange) -> Unit,
) {
    SheetFieldLabel(text = stringResource(R.string.transaction_category_icon))
    ChoiceGrid(keys = FintrackIcons.categoryKeys, columns = GRID_COLUMNS) { key ->
        IconChoice(
            look = IconLook(iconKey = key, colorKey = form.colorKey),
            selected = key == form.iconKey,
            onSelect = { change(CategoryChange.IconChanged(key)) },
        )
    }
    SheetFieldLabel(text = stringResource(R.string.transaction_category_color))
    ChoiceGrid(keys = COLOR_KEYS, columns = GRID_COLUMNS) { key ->
        ColorSwatch(
            colorKey = key,
            selected = key == form.colorKey,
            onSelect = { change(CategoryChange.ColorChanged(key)) },
        )
    }
}
