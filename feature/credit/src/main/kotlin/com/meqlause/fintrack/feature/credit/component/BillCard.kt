package com.meqlause.fintrack.feature.credit.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.credit.StatementSummary
import com.meqlause.fintrack.core.model.StatementStatus
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.credit.R
import com.meqlause.fintrack.feature.credit.detail.CreditDetailUiState
import com.meqlause.fintrack.feature.credit.detail.CreditLabels
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

// FR-131, FR-136, S-14: the current bill with its due countdown and status chip, and the Bayar tagihan button
@Composable
internal fun BillCard(
    state: CreditDetailUiState.Success,
    onPay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statement = state.statement
    FintrackCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.credit_bill_title, monthLabel(statement)),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = MoneyFormatter.format(statement.breakdown.remainingDue),
            style = FintrackTheme.typography.numberLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Text(
            text = dueLine(statement, state.today),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = stringResource(CreditLabels.status(statement.status)),
            style = FintrackTheme.typography.labelLarge,
            color = statusColor(statement.status),
        )
        FintrackButton(text = stringResource(R.string.credit_pay_button), onClick = onPay)
    }
}

@Composable
private fun dueLine(
    statement: StatementSummary,
    today: LocalDate,
): String {
    val due = statement.period.dueDate
    val days = ChronoUnit.DAYS.between(today, due)
    val countdown =
        when {
            days > 0L -> stringResource(R.string.credit_due_in_days, days.toInt())
            days == 0L -> stringResource(R.string.credit_due_today)
            else -> stringResource(R.string.credit_overdue_days, (-days).toInt())
        }
    return stringResource(R.string.credit_due_date, DUE_FORMAT.format(due)) + " · " + countdown
}

@Composable
private fun statusColor(status: StatementStatus) =
    when (status) {
        StatementStatus.OVERDUE -> FintrackTheme.colors.semantics.error
        StatementStatus.PAID -> FintrackTheme.colors.semantics.income
        else -> FintrackTheme.colors.texts.onSurfaceVariant
    }

private fun monthLabel(statement: StatementSummary): String = MONTH_FORMAT.format(statement.period.month)

private val MONTH_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM", Locale.forLanguageTag("id"))
private val DUE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("id"))
