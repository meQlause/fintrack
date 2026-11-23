package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.NumericKeypad
import com.meqlause.fintrack.core.designsystem.component.SheetHeader
import com.meqlause.fintrack.core.designsystem.component.SheetTextField
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.feature.transaction.history.HistorySegmentOption
import com.meqlause.fintrack.feature.transaction.history.HistorySegmented

private val TYPE_ORDER = listOf(TransactionType.EXPENSE, TransactionType.INCOME, TransactionType.TRANSFER)

// FR-030..FR-033, S-04: the scrolling body of the sheet, from the title row down to Simpan
@Composable
internal fun EditTransactionFormBody(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = FintrackTheme.spacing
    Column(modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        EditTransactionHeader(state = state, onEvent = onEvent, onClose = onClose)
        HistorySegmented(
            options = typeOptions(),
            selected = if (state.loan != null) TYPE_ORDER.size else TYPE_ORDER.indexOf(state.form.type),
            onPick = { index -> onEvent(typeTabEvent(index)) },
            modifier = Modifier.padding(top = spacing.small),
            trackHeight = FintrackScreenSizes.segmentTall,
        )
        EditTransactionAmountBlock(state = state, onEvent = onEvent)
        if (state.loan != null) {
            EditLoanRows(state = state, onEvent = onEvent)
        } else {
            EditTransactionRoutingRows(state = state, onEvent = onEvent)
        }
        SheetTextField(
            value = state.form.note,
            onValueChange = { note -> onEvent(EditTransactionEvent.NoteChanged(note)) },
            placeholder = stringResource(R.string.transaction_note_hint),
            modifier = Modifier.padding(top = spacing.medium),
        )
        NumericKeypad(
            onDigit = { digit -> onEvent(EditTransactionEvent.DigitTyped(digit)) },
            onBackspace = { onEvent(EditTransactionEvent.BackspacePressed) },
            modifier = Modifier.padding(top = spacing.medium),
        )
        if (state.canKeepShortcut) {
            EditShortcutKeepRow(state = state, onEvent = onEvent, modifier = Modifier.padding(top = spacing.medium))
        }
        FintrackButton(
            text = stringResource(R.string.transaction_save),
            onClick = { onEvent(EditTransactionEvent.SaveRequested) },
            enabled = state.canSave,
            modifier = Modifier.padding(top = FintrackScreenSizes.sheetTop),
        )
    }
}

// S-04, FR-039: the sheet title, the "Pintasan" chip that opens the shortcut drawer for a new entry, and close
@Composable
private fun EditTransactionHeader(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    onClose: () -> Unit,
) {
    val titleRes =
        when {
            state.isEditing -> R.string.transaction_title_edit
            state.loan != null -> R.string.transaction_title_loan
            else -> R.string.transaction_title_add
        }
    SheetHeader(title = stringResource(titleRes), onClose = onClose) {
        if (!state.isEditing && state.loan == null && !state.form.isTransfer) {
            ShortcutChip(
                onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.SHORTCUTS)) },
                modifier = Modifier.padding(end = FintrackTheme.spacing.xxSmall),
            )
        }
    }
}

// S-04, DOC-08: Keluar, Masuk, Transfer and Pinjaman; the picked tab reads in its type colour
@Composable
private fun typeOptions(): List<HistorySegmentOption<Int>> {
    val colors = FintrackTheme.colors
    return listOf(
        HistorySegmentOption(0, stringResource(R.string.history_type_expense), activeColor = colors.semantics.expense),
        HistorySegmentOption(1, stringResource(R.string.history_type_income), activeColor = colors.semantics.income),
        HistorySegmentOption(
            2,
            stringResource(R.string.transaction_type_transfer),
            activeColor = colors.semantics.transfer,
        ),
        HistorySegmentOption(3, stringResource(R.string.transaction_type_loan), activeColor = colors.brands.primary),
    )
}

// S-04: the Pinjaman tab follows the three type tabs; every other index picks its transaction type
private fun typeTabEvent(index: Int): EditTransactionEvent =
    if (index == TYPE_ORDER.size) {
        EditTransactionEvent.LoanEdited(LoanChange.TabSelected)
    } else {
        EditTransactionEvent.TypeSelected(TYPE_ORDER[index])
    }
