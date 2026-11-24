package com.meqlause.fintrack.feature.transaction.shortcut

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.SheetFieldLabel
import com.meqlause.fintrack.core.designsystem.component.SheetHeader
import com.meqlause.fintrack.core.designsystem.component.SheetNumberField
import com.meqlause.fintrack.core.designsystem.component.SheetTextField
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.feature.transaction.history.HistorySegmentOption
import com.meqlause.fintrack.feature.transaction.history.HistorySegmented
import com.meqlause.fintrack.feature.transaction.history.SegmentChoice

// FR-039, S-03: the scrolling form from the title down to Simpan; it saves a shortcut, never a transaction
@Composable
internal fun ShortcutFormBody(
    state: ShortcutFormUiState,
    onEvent: (ShortcutFormEvent) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val titleRes = if (state.isEditing) R.string.shortcut_form_title_edit else R.string.shortcut_form_title_new
    Column(modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        SheetHeader(title = stringResource(titleRes), onClose = onClose)
        ShortcutTypeSwitch(
            type = state.draft.type,
            onPick = { onEvent(ShortcutFormEvent.TypeSelected(it)) },
            modifier = Modifier.padding(top = FintrackTheme.spacing.small),
        )
        ShortcutTextFields(state = state, onEvent = onEvent)
        ShortcutPickRows(state = state, onEvent = onEvent)
        SheetFieldLabel(text = stringResource(R.string.shortcut_form_note_label))
        SheetTextField(
            value = state.draft.note,
            onValueChange = { onEvent(ShortcutFormEvent.NoteChanged(it)) },
            placeholder = stringResource(R.string.shortcut_form_note_hint),
        )
        ShortcutErrors(state = state)
        FintrackButton(
            text = stringResource(R.string.transaction_save),
            onClick = { onEvent(ShortcutFormEvent.SaveRequested) },
            enabled = state.canSave,
            modifier = Modifier.padding(top = FintrackScreenSizes.sheetTop),
        )
    }
}

// FR-039: the shortcut's own Nama, shown on the tile, and its amount
@Composable
private fun ShortcutTextFields(
    state: ShortcutFormUiState,
    onEvent: (ShortcutFormEvent) -> Unit,
) {
    SheetFieldLabel(text = stringResource(R.string.shortcut_form_name_label))
    SheetTextField(
        value = state.draft.name,
        onValueChange = { onEvent(ShortcutFormEvent.NameChanged(it)) },
        placeholder = stringResource(R.string.shortcut_form_name_hint),
    )
    SheetFieldLabel(text = stringResource(R.string.shortcut_form_amount_label))
    SheetNumberField(
        value = state.draft.rupiahDigits,
        onValueChange = { onEvent(ShortcutFormEvent.AmountChanged(it)) },
        placeholder = stringResource(R.string.shortcut_form_amount_hint),
    )
}

// FR-039: the message of the first rule the last save broke
@Composable
private fun ShortcutErrors(state: ShortcutFormUiState) {
    state.status.errors.minByOrNull { it.ordinal }?.let { error ->
        Text(
            text = stringResource(error.messageRes()),
            style = FintrackScreenText.style(size = 12f, weight = 600),
            color = FintrackTheme.colors.semantics.error,
            modifier = Modifier.padding(top = FintrackTheme.spacing.small),
        )
    }
}

// FR-039: only an expense or an income can become a shortcut; the picked tab reads in its type colour
@Composable
private fun ShortcutTypeSwitch(
    type: TransactionType,
    onPick: (TransactionType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    HistorySegmented(
        options =
            listOf(
                HistorySegmentOption(
                    TransactionType.EXPENSE,
                    stringResource(R.string.history_type_expense),
                    activeColor = colors.semantics.expense,
                ),
                HistorySegmentOption(
                    TransactionType.INCOME,
                    stringResource(R.string.history_type_income),
                    activeColor = colors.semantics.income,
                ),
            ),
        choice = SegmentChoice(selected = type, onPick = onPick),
        modifier = modifier,
        trackHeight = FintrackScreenSizes.segmentTall,
    )
}
