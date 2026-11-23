package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.icon.IconCircle
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.StatementStatus

// FR-137, S-03: one credit bill with its name, due line, amount and status; tap opens the credit wallet (S-14)
@Composable
internal fun HomeBillRow(
    bill: HomeBill,
    hidden: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = FintrackTheme.sizes.rowInset, vertical = FintrackTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
    ) {
        IconCircle(look = IconLook(iconKey = bill.appearance.iconKey, colorKey = bill.appearance.colorKey))
        BillName(bill = bill, modifier = Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            HomeAmountText(
                money = bill.amount,
                hidden = hidden,
                style = FintrackTheme.typography.numberRow.copy(fontWeight = FontWeight.Bold),
            )
            StatusChip(status = bill.status)
        }
    }
}

// FR-137: the bill name over its due line; a paid bill keeps the due line in the muted text colour
@Composable
private fun BillName(
    bill: HomeBill,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = bill.name,
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = dueText(bill),
            style = FintrackTheme.typography.labelMedium,
            color = dueColor(bill.status),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// FR-137: the due line is muted once the bill is paid, otherwise it takes the status colour
@Composable
private fun dueColor(status: StatementStatus): Color =
    if (status == StatementStatus.PAID) FintrackTheme.colors.texts.onSurfaceVariant else statusColor(status)

// FR-137, BR-034: the due line of a bill in the words of its status
@Composable
private fun dueText(bill: HomeBill): String =
    when (bill.status) {
        StatementStatus.NOT_ISSUED -> stringResource(R.string.home_bill_due_first, bill.dateLabel)
        StatementStatus.DUE -> stringResource(R.string.home_bill_due_date, bill.dateLabel)
        StatementStatus.PAID -> stringResource(R.string.home_bill_next_statement, bill.dateLabel)
        StatementStatus.OVERDUE -> stringResource(R.string.home_bill_due_overdue, bill.dateLabel)
    }

// BR-034: the status chip text, Indonesian copy of NOT_ISSUED, PAID, DUE and OVERDUE
@Composable
private fun StatusChip(status: StatementStatus) {
    val tone = statusColor(status)
    Text(
        text = statusText(status),
        modifier =
            Modifier
                .clip(FintrackTheme.shapes.small)
                .background(tone.copy(alpha = CHIP_ALPHA))
                .padding(horizontal = FintrackTheme.spacing.xSmall, vertical = FintrackTheme.spacing.xxSmall),
        style = FintrackTheme.typography.labelMedium,
        color = tone,
    )
}

@Composable
private fun statusText(status: StatementStatus): String =
    when (status) {
        StatementStatus.NOT_ISSUED -> stringResource(R.string.home_bill_status_not_issued)
        StatementStatus.PAID -> stringResource(R.string.home_bill_status_paid)
        StatementStatus.DUE -> stringResource(R.string.home_bill_status_due)
        StatementStatus.OVERDUE -> stringResource(R.string.home_bill_status_overdue)
    }

@Composable
private fun statusColor(status: StatementStatus): Color =
    when (status) {
        StatementStatus.OVERDUE -> FintrackTheme.colors.semantics.error
        StatementStatus.DUE -> FintrackTheme.colors.semantics.warning
        StatementStatus.PAID -> FintrackTheme.colors.semantics.income
        StatementStatus.NOT_ISSUED -> FintrackTheme.colors.texts.onSurfaceVariant
    }

private const val CHIP_ALPHA = 0.12f
