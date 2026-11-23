package com.meqlause.fintrack.feature.transaction.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    detail.categoryName?.let {
        DetailRow(label = stringResource(R.string.transaction_detail_category), value = it, isFirst = true)
    }
    DetailRow(label = stringResource(walletLabel), value = detail.walletLabel, isFirst = detail.categoryName == null)
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

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isFirst: Boolean = false,
) {
    if (!isFirst) {
        HorizontalDivider(thickness = FintrackTheme.sizes.hairline, color = FintrackTheme.colors.surfaces.outline)
    }
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = FintrackTheme.sizes.rowInset, vertical = FintrackTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = FintrackTheme.typography.tabWide.copy(fontWeight = FontWeight.Normal),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = value,
            style = FintrackTheme.typography.tabWide,
            color = FintrackTheme.colors.texts.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f).padding(start = FintrackTheme.spacing.medium),
        )
    }
}

@StringRes
private fun sourceLabel(source: TransactionSource): Int =
    when (source) {
        TransactionSource.MANUAL -> R.string.transaction_detail_source_manual
        TransactionSource.NOTIFICATION -> R.string.transaction_detail_source_notification
    }
