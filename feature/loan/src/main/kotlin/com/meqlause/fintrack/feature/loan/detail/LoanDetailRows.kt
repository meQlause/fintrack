package com.meqlause.fintrack.feature.loan.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.component.FormRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.loan.LoanEntry
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.loan.R
import com.meqlause.fintrack.feature.loan.list.RepaymentBar
import com.meqlause.fintrack.feature.loan.loanDateLabel
import java.time.ZoneId

// S-17: the header, amounts, progress, due date, timeline and the actions of one loan
@Composable
internal fun LoanDetailBody(
    entry: LoanEntry,
    state: LoanDetailUiState,
    onEvent: (LoanDetailEvent) -> Unit,
) {
    val loan = entry.loan
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall)) {
        Text(
            text = loan.agreement.personName,
            style = FintrackTheme.typography.headlineSmall,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Text(
            text = stringResource(statusRes(loan.status, loan.agreement.direction)),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
    AmountBlock(entry = entry)
    RepaymentBar(progress = progressOf(entry))
    DueRow(entry = entry, canChange = state.canRepay, onEvent = onEvent)
    TimelineCard(movements = entry.movements, personName = loan.agreement.personName)
    LoanActions(state = state, onEvent = onEvent)
}

// S-17: the outstanding amount in large type with the principal under it
@Composable
private fun AmountBlock(entry: LoanEntry) {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall)) {
        Text(
            text = stringResource(R.string.loan_outstanding_label),
            style = FintrackTheme.typography.labelMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = MoneyFormatter.format(entry.loanBalance.outstanding),
            style = FintrackTheme.typography.headlineSmall,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Text(
            text = stringResource(R.string.loan_principal, MoneyFormatter.format(entry.loanBalance.principal)),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}

// FR-158: the due date as a row that opens the due-date drawer while the loan is open
@Composable
private fun DueRow(
    entry: LoanEntry,
    canChange: Boolean,
    onEvent: (LoanDetailEvent) -> Unit,
) {
    val dueOn =
        entry.loan.agreement.dueDate
            ?.atZone(ZoneId.systemDefault())
            ?.toLocalDate()
    val value = dueOn?.loanDateLabel() ?: stringResource(R.string.loan_due_none)
    if (canChange) {
        FormRow(
            label = stringResource(R.string.loan_due_change),
            value = value,
            onClick = { onEvent(LoanDetailEvent.OpenDueDate) },
        )
    } else {
        Text(
            text = value,
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}

// S-17: every movement of the loan, the opening one first, in the order it happened
@Composable
private fun TimelineCard(
    movements: List<Transaction>,
    personName: String,
) {
    FintrackCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.loan_timeline_title),
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        movements.forEach { movement ->
            MovementRow(movement = movement, personName = personName)
        }
    }
}

// S-17: one movement with its label, its date and its amount
@Composable
private fun MovementRow(
    movement: Transaction,
    personName: String,
) {
    val date =
        movement.timing.occurredAt
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(movementLabelRes(movement.movement.type), personName),
                style = FintrackTheme.typography.bodyMedium,
                color = FintrackTheme.colors.texts.onSurface,
            )
            Text(
                text = date.loanDateLabel(),
                style = FintrackTheme.typography.labelMedium,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
        }
        Text(
            text = MoneyFormatter.format(movement.movement.amount),
            style = FintrackTheme.typography.bodyLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

// S-17: repay while open, write off a LENT loan, or delete the loan with its repayments (BR-046)
@Composable
private fun LoanActions(
    state: LoanDetailUiState,
    onEvent: (LoanDetailEvent) -> Unit,
) {
    if (state.canRepay) {
        FintrackButton(
            text = stringResource(R.string.loan_repay),
            onClick = { onEvent(LoanDetailEvent.OpenRepayment) },
        )
    }
    if (state.canWriteOff) {
        TextButton(onClick = { onEvent(LoanDetailEvent.DialogOpened(LoanDialog.WRITE_OFF)) }) {
            Text(text = stringResource(R.string.loan_write_off))
        }
    }
    TextButton(onClick = { onEvent(LoanDetailEvent.DialogOpened(LoanDialog.DELETE)) }) {
        Text(text = stringResource(R.string.loan_delete))
    }
}

// FR-150, FR-151: the caption of the loan, Piutang or Hutang, with its status while it is still open
private fun statusRes(
    status: LoanStatus,
    direction: LoanDirection,
): Int =
    when (status) {
        LoanStatus.SETTLED -> {
            R.string.loan_status_settled
        }

        LoanStatus.WRITTEN_OFF -> {
            R.string.loan_status_written_off
        }

        LoanStatus.OPEN -> {
            if (direction ==
                LoanDirection.LENT
            ) {
                R.string.loan_status_lent
            } else {
                R.string.loan_status_borrowed
            }
        }
    }

// S-17: the words of a movement: lent, borrowed, paid back to us, or paid back by us
private fun movementLabelRes(type: TransactionType): Int =
    when (type) {
        TransactionType.BORROW -> R.string.loan_movement_borrow
        TransactionType.COLLECT -> R.string.loan_movement_collect
        TransactionType.REPAY -> R.string.loan_movement_repay
        else -> R.string.loan_movement_lend
    }

// FR-153: the share repaid so far, used by the progress bar of the detail
private fun progressOf(entry: LoanEntry): Float {
    val principal = entry.loanBalance.principal.rupiah
    return if (principal <= 0L) {
        0f
    } else {
        (
            entry.loanBalance.repaid.rupiah
                .toFloat() / principal
        ).coerceIn(0f, 1f)
    }
}
