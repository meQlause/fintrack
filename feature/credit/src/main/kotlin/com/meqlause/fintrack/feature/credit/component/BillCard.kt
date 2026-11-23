package com.meqlause.fintrack.feature.credit.component

import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.luminance
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.component.ScreenChip
import com.meqlause.fintrack.core.designsystem.component.ScreenPrimaryButton
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
    val colors = FintrackTheme.colors
    val status = statement.status
    ScreenCard(modifier = modifier) {
        Column(modifier = Modifier.padding(FintrackScreenSizes.billInset)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.credit_bill_title, monthLabel(statement)),
                    style = FintrackScreenText.label(),
                    color = colors.texts.onSurfaceVariant,
                )
                StatusChip(status = status)
            }
            Text(
                text = MoneyFormatter.format(statement.breakdown.remainingDue),
                style = FintrackScreenText.number(size = 34f, weight = 700),
                color = colors.texts.onSurface,
                modifier = Modifier.padding(top = FintrackTheme.spacing.xSmall + FintrackTheme.spacing.xxSmall),
            )
            Text(
                text = dueLine(statement, state.today),
                style = FintrackScreenText.style(size = 12.5f, weight = 600),
                color = statusColor(status),
                modifier = Modifier.padding(top = FintrackTheme.spacing.xSmall),
            )
            Text(
                text =
                    stringResource(
                        R.string.credit_bill_detail,
                        MoneyFormatter.format(statement.breakdown.statementAmount),
                        MoneyFormatter.format(statement.breakdown.paidSinceCutoff),
                    ),
                style = FintrackScreenText.rowSubNumber(),
                color = colors.texts.onSurfaceVariant,
                modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
            )
            ScreenPrimaryButton(
                text = stringResource(R.string.credit_pay_button),
                onClick = onPay,
                modifier = Modifier.padding(top = FintrackScreenSizes.rowPaddingHorizontal),
            )
        }
    }
}

// S-14: the status pill, amber while due, orange when late, teal once paid and grey before the statement
@Composable
private fun StatusChip(status: StatementStatus) {
    val colors = FintrackTheme.colors
    val isDark = colors.surfaces.background.luminance() < HALF
    val alpha = if (isDark) FintrackTheme.alphas.categoryTintDark else FintrackTheme.alphas.categoryTintLight
    val (background, foreground) =
        when (status) {
            StatementStatus.DUE -> colors.semantics.warning.copy(alpha = alpha) to colors.semantics.warning
            StatementStatus.OVERDUE -> colors.semantics.error.copy(alpha = alpha) to colors.semantics.error
            StatementStatus.PAID -> colors.brands.primaryContainer to colors.brands.onPrimaryContainer
            StatementStatus.NOT_ISSUED -> colors.surfaces.surfaceVariant to colors.texts.onSurfaceVariant
        }
    ScreenChip(text = stringResource(CreditLabels.status(status)), background = background, foreground = foreground)
}

private const val HALF = 0.5f

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
        StatementStatus.DUE -> FintrackTheme.colors.semantics.warning
        else -> FintrackTheme.colors.texts.onSurfaceVariant
    }

private fun monthLabel(statement: StatementSummary): String = MONTH_FORMAT.format(statement.period.month)

private val MONTH_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM", Locale.forLanguageTag("id"))
private val DUE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("id"))
