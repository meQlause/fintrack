package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.NumericKeypad
import com.meqlause.fintrack.core.designsystem.component.SegmentedControl
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.core.ui.R as UiR

private val TYPE_ORDER = listOf(TransactionType.EXPENSE, TransactionType.INCOME, TransactionType.TRANSFER)

// FR-030..FR-033, S-04: the scrolling body of the sheet, from the type tabs down to Simpan
@Composable
internal fun EditTransactionFormBody(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
    ) {
        EditTransactionHeader(state = state, onEvent = onEvent)
        SegmentedControl(
            options = typeLabels(),
            selectedIndex = TYPE_ORDER.indexOf(state.form.type),
            onSelect = { index -> onEvent(EditTransactionEvent.TypeSelected(TYPE_ORDER[index])) },
        )
        EditTransactionAmountBlock(state = state, onEvent = onEvent)
        EditTransactionRoutingRows(state = state, onEvent = onEvent)
        OutlinedTextField(
            value = state.form.note,
            onValueChange = { note -> onEvent(EditTransactionEvent.NoteChanged(note)) },
            placeholder = { Text(text = stringResource(R.string.transaction_note_hint)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        NumericKeypad(
            onDigit = { digit -> onEvent(EditTransactionEvent.DigitTyped(digit)) },
            onBackspace = { onEvent(EditTransactionEvent.BackspacePressed) },
        )
        if (state.canKeepShortcut) {
            EditShortcutKeepRow(state = state, onEvent = onEvent)
        }
        FintrackButton(
            text = stringResource(R.string.transaction_save),
            onClick = { onEvent(EditTransactionEvent.SaveRequested) },
            enabled = state.canSave,
        )
    }
}

// S-04, FR-039: the sheet title, with the "⚡ Pintasan" button that opens the shortcut drawer for a new entry
@Composable
private fun EditTransactionHeader(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text =
                stringResource(
                    if (state.isEditing) R.string.transaction_title_edit else R.string.transaction_title_add,
                ),
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (!state.isEditing) {
            TextButton(onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.SHORTCUTS)) }) {
                Text(text = stringResource(UiR.string.shortcut_drawer_title))
            }
        }
    }
}

// S-04: the three type tabs in the order TYPE_ORDER lists them
@Composable
private fun typeLabels(): List<String> =
    listOf(
        stringResource(R.string.transaction_type_expense),
        stringResource(R.string.transaction_type_income),
        stringResource(R.string.transaction_type_transfer),
    )
