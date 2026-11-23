package com.meqlause.fintrack.feature.loan.list

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.loan.R
import com.meqlause.fintrack.feature.loan.loanDateLabel

// FR-153: one person and the open loans to or from them, each row opening its detail
@Composable
internal fun PersonGroupItem(
    group: LoanPersonGroup,
    onOpenLoan: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall),
    ) {
        Text(
            text = group.personName,
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        group.rows.forEach { row ->
            LoanRowItem(loanRow = row, onClick = { onOpenLoan(row.loanId) })
        }
    }
}

// S-17: outstanding amount, progress and the due line of one loan
@Composable
internal fun LoanRowItem(
    loanRow: LoanListRow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dueColor =
        if (loanRow.overdue) FintrackTheme.colors.semantics.error else FintrackTheme.colors.texts.onSurfaceVariant
    FintrackCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall),
            ) {
                Text(
                    text = dueText(loanRow),
                    style = FintrackTheme.typography.labelMedium,
                    color = dueColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                RepaymentBar(progress = loanRow.progress)
            }
            Text(
                text = MoneyFormatter.format(loanRow.outstanding),
                style = FintrackTheme.typography.bodyLarge,
                color = FintrackTheme.colors.texts.onSurface,
            )
        }
    }
}

// FR-153: the progress of repayments; the track is the surface variant, the fill the brand colour
@Composable
internal fun RepaymentBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val track = FintrackTheme.colors.surfaces.surfaceVariant
    val fill = FintrackTheme.colors.brands.primary
    Canvas(modifier = modifier.fillMaxWidth().height(FintrackTheme.spacing.xSmall)) {
        drawRect(color = track)
        drawRect(color = fill, size = size.copy(width = size.width * progress.coerceIn(0f, 1f)))
    }
}

// FR-154, FR-158: what the row says about the due date, or about a loan that is no longer open
@Composable
private fun dueText(loanRow: LoanListRow): String =
    when {
        loanRow.status == LoanStatus.SETTLED -> stringResource(R.string.loan_status_settled)
        loanRow.status == LoanStatus.WRITTEN_OFF -> stringResource(R.string.loan_status_written_off)
        loanRow.overdue -> stringResource(R.string.loan_due_overdue)
        loanRow.dueDate == null -> stringResource(R.string.loan_due_none)
        else -> stringResource(R.string.loan_due_on, loanRow.dueDate.loanDateLabel())
    }
