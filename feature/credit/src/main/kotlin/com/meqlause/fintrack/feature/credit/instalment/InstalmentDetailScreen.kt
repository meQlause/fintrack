package com.meqlause.fintrack.feature.credit.instalment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.ProgressBar
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.credit.R

// FR-142, FR-144, FR-145, S-16: one plan with its schedule, the early payoff and the delete confirmation
@Composable
internal fun InstalmentDetailScreen(
    state: InstalmentDetailUiState,
    callbacks: InstalmentDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(FintrackTheme.colors.surfaces.background)
                .padding(horizontal = FintrackTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        Text(
            text = stringResource(R.string.credit_back),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.brands.primary,
            modifier = Modifier.clickable(onClick = callbacks.onBack).padding(vertical = FintrackTheme.spacing.small),
        )
        when (state) {
            InstalmentDetailUiState.Loading, InstalmentDetailUiState.Closed -> Unit
            InstalmentDetailUiState.Missing -> MissingNote()
            is InstalmentDetailUiState.Success -> InstalmentDetailBody(state = state, callbacks = callbacks)
        }
    }
}

@Composable
private fun MissingNote() {
    Text(
        text = stringResource(R.string.instalment_detail_missing),
        style = FintrackTheme.typography.bodyLarge,
        color = FintrackTheme.colors.texts.onSurfaceVariant,
    )
}

// S-16: the summary, the portions with their statements, then the payoff and delete actions
@Composable
private fun ColumnScope.InstalmentDetailBody(
    state: InstalmentDetailUiState.Success,
    callbacks: InstalmentDetailCallbacks,
) {
    val row = state.row
    LazyColumn(
        modifier = Modifier.fillMaxWidth().weight(1f),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        item { InstalmentSummary(row = row) }
        item { ActionRows(row = row, callbacks = callbacks) }
        item {
            Text(
                text = stringResource(R.string.instalment_schedule_title),
                style = FintrackTheme.typography.labelLarge,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
        }
        items(items = row.portions, key = { it.number }) { portion -> PortionLine(portion = portion) }
    }
    if (state.confirmDelete) {
        DeleteDialog(onConfirm = callbacks.onConfirmDelete, onDismiss = callbacks.onDismissDelete)
    }
}

// FR-142: title, card, monthly portion, progress "k/n" and what remains
@Composable
private fun InstalmentSummary(row: InstalmentRow) {
    val colors = FintrackTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        Text(text = row.title, style = FintrackTheme.typography.headlineSmall, color = colors.texts.onSurface)
        Text(
            text = "${row.cardName} · ${stringResource(
                R.string.instalment_monthly,
                MoneyFormatter.format(row.monthly),
            )}",
            style = FintrackTheme.typography.bodyMedium,
            color = colors.texts.onSurfaceVariant,
        )
        ProgressBar(fraction = row.billed.toFloat() / row.tenor, color = colors.brands.primary)
        Text(
            text = stringResource(R.string.instalment_progress, row.billed, row.tenor),
            style = FintrackTheme.typography.labelLarge,
            color = colors.texts.onSurface,
        )
        Text(
            text = stringResource(R.string.instalment_remaining_full, MoneyFormatter.format(row.remaining)),
            style = FintrackTheme.typography.bodyMedium,
            color = colors.texts.onSurfaceVariant,
        )
        row.payoffMonth?.let { month ->
            Text(
                text = stringResource(R.string.instalment_pay_off_set, month.label()),
                style = FintrackTheme.typography.labelMedium,
                color = colors.texts.onSurfaceVariant,
            )
        }
    }
}

// FR-144, FR-145: "Lunasi sekarang" is offered while portions remain; "Hapus" asks first
@Composable
private fun ActionRows(
    row: InstalmentRow,
    callbacks: InstalmentDetailCallbacks,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        FintrackButton(
            text = stringResource(R.string.instalment_pay_off),
            onClick = callbacks.onPayOff,
            modifier = Modifier.fillMaxWidth(),
            enabled = row.canPayOff,
        )
        TextButton(onClick = callbacks.onRequestDelete) {
            Text(text = stringResource(R.string.instalment_delete), color = FintrackTheme.colors.texts.onSurface)
        }
    }
}

// S-16: one portion, its statement month, amount and whether that statement has billed it
@Composable
private fun PortionLine(portion: InstalmentPortionRow) {
    val colors = FintrackTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = FintrackTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.instalment_portion_title, portion.number),
                style = FintrackTheme.typography.bodyMedium,
                color = colors.texts.onSurface,
            )
            Text(
                text = portion.statementMonth.label(),
                style = FintrackTheme.typography.labelMedium,
                color = colors.texts.onSurfaceVariant,
            )
        }
        Text(
            text = MoneyFormatter.format(portion.amount),
            style = FintrackTheme.typography.bodyMedium,
            color = colors.texts.onSurface,
        )
        Text(
            text =
                stringResource(
                    if (portion.billed) R.string.instalment_portion_billed else R.string.instalment_portion_pending,
                ),
            style = FintrackTheme.typography.labelMedium,
            color = colors.texts.onSurfaceVariant,
        )
    }
}

@Composable
private fun DeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.instalment_delete_title)) },
        text = { Text(text = stringResource(R.string.instalment_delete_text)) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(text = stringResource(R.string.instalment_delete_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(R.string.credit_pay_cancel)) }
        },
    )
}
