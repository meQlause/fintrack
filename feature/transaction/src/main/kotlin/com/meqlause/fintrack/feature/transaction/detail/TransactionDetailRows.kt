package com.meqlause.fintrack.feature.transaction.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R

// FR-034, S-06: the labelled facts of one transaction, one row each; a transfer shows its route instead of a wallet
@Composable
internal fun DetailRows(detail: TransactionDetail) {
    val walletLabel =
        if (detail.type == TransactionType.TRANSFER) {
            R.string.transaction_detail_route
        } else {
            R.string.transaction_detail_wallet
        }
    DetailRow(label = stringResource(walletLabel), value = detail.walletLabel)
    detail.categoryName?.let { DetailRow(label = stringResource(R.string.transaction_detail_category), value = it) }
    if (!detail.feeAmount.isZero) {
        DetailRow(
            label = stringResource(R.string.transaction_fee_label),
            value = MoneyFormatter.format(detail.feeAmount),
        )
    }
    DetailRow(
        label = stringResource(R.string.transaction_detail_time),
        value = "${detail.dateLabel}, ${detail.timeLabel}",
    )
    if (detail.merchant.isNotBlank()) {
        DetailRow(label = stringResource(R.string.transaction_detail_merchant), value = detail.merchant)
    }
    DetailRow(
        label = stringResource(R.string.transaction_detail_note),
        value = detail.note.ifBlank { stringResource(R.string.transaction_detail_empty) },
    )
    DetailRow(
        label = stringResource(R.string.transaction_detail_source),
        value = stringResource(sourceLabel(detail.source)),
    )
}

// FR-034, FR-035, FR-038: Ubah, Catat lagi (not for loan-linked rows) and Hapus, sharing the row equally
@Composable
internal fun ActionRow(
    isLoanLinked: Boolean,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        ActionCard(
            text = stringResource(R.string.transaction_detail_edit),
            onClick = onEdit,
            modifier = Modifier.weight(1f),
        )
        if (!isLoanLinked) {
            ActionCard(
                text = stringResource(R.string.transaction_detail_duplicate),
                onClick = onDuplicate,
                modifier = Modifier.weight(1f),
            )
        }
        ActionCard(
            text = stringResource(R.string.transaction_detail_delete),
            onClick = onDelete,
            modifier = Modifier.weight(1f),
            color = FintrackTheme.colors.semantics.error,
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = FintrackTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = value,
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f).padding(start = FintrackTheme.spacing.medium),
        )
    }
}

@Composable
private fun ActionCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = FintrackTheme.colors.texts.onSurface,
) {
    FintrackCard(modifier = modifier, onClick = onClick) {
        Text(
            text = text,
            style = FintrackTheme.typography.labelLarge,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = FintrackTheme.spacing.medium),
        )
    }
}

@StringRes
private fun sourceLabel(source: TransactionSource): Int =
    when (source) {
        TransactionSource.MANUAL -> R.string.transaction_detail_source_manual
        TransactionSource.NOTIFICATION -> R.string.transaction_detail_source_notification
    }
