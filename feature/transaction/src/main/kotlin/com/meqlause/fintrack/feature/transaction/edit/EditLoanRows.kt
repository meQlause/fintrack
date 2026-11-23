package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FormRow
import com.meqlause.fintrack.core.designsystem.component.SegmentedControl
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.feature.transaction.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DUE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("id"))

// FR-150, FR-151, FR-157, FR-158, S-04: the Pinjaman block; direction, person, wallet and the optional due date
@Composable
internal fun EditLoanRows(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val loan = state.loan ?: return
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        SegmentedControl(
            options =
                listOf(
                    stringResource(R.string.loan_direction_lent),
                    stringResource(R.string.loan_direction_borrowed),
                ),
            selectedIndex = if (loan.direction == LoanDirection.LENT) 0 else 1,
            onSelect = { index ->
                val picked = if (index == 0) LoanDirection.LENT else LoanDirection.BORROWED
                onEvent(EditTransactionEvent.LoanEdited(LoanChange.DirectionPicked(picked)))
            },
        )
        OutlinedTextField(
            value = loan.personName,
            onValueChange = { name -> onEvent(EditTransactionEvent.LoanEdited(LoanChange.PersonChanged(name))) },
            placeholder = { Text(text = stringResource(R.string.loan_person_hint)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        FormRow(
            label = stringResource(walletLabelRes(loan.direction)),
            value = walletName(state, state.form.walletId),
            onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.WALLET)) },
        )
        FormRow(
            label = stringResource(R.string.loan_due_label),
            value = dueLabel(loan.dueDate),
            onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.LOAN_DUE)) },
        )
    }
}

// FR-150, FR-151: a lent amount leaves the wallet, a borrowed amount comes into it
private fun walletLabelRes(direction: LoanDirection): Int =
    if (direction == LoanDirection.LENT) R.string.loan_wallet_lent else R.string.loan_wallet_borrowed

// FR-158: the chosen due date, or the words "Tidak ada"
@Composable
private fun dueLabel(dueDate: LocalDate?): String =
    dueDate?.format(DUE_FORMAT) ?: stringResource(R.string.loan_due_none)
