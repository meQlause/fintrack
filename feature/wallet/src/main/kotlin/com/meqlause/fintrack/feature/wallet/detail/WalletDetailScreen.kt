package com.meqlause.fintrack.feature.wallet.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.core.ui.transaction.TransactionRow
import com.meqlause.fintrack.core.ui.transaction.TransactionRowCallbacks
import com.meqlause.fintrack.core.ui.transaction.TransactionRowParams
import com.meqlause.fintrack.feature.wallet.R
import com.meqlause.fintrack.feature.wallet.WalletLabels
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-017, S-19: stateless wallet detail; header, balance, Pakai and Transfer, then the recent transactions
@Composable
internal fun WalletDetailScreen(
    state: WalletDetailUiState,
    callbacks: WalletDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(FintrackTheme.colors.surfaces.background)
                .padding(horizontal = FintrackTheme.spacing.large),
    ) {
        Text(
            text = stringResource(R.string.wallet_detail_back),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.brands.primary,
            modifier = Modifier.clickable(onClick = callbacks.onBack).padding(vertical = FintrackTheme.spacing.small),
        )
        when (state) {
            WalletDetailUiState.Loading -> Unit
            WalletDetailUiState.Missing -> MissingText()
            is WalletDetailUiState.Success -> WalletDetailBody(state = state, callbacks = callbacks)
        }
    }
}

// S-19: the body of an existing wallet; the recent list shows eight rows until "Lihat semua" expands it
@Composable
private fun WalletDetailBody(
    state: WalletDetailUiState.Success,
    callbacks: WalletDetailCallbacks,
) {
    val visible = if (state.expanded) state.rows else state.rows.take(RECENT_LIMIT)
    LazyColumn(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        item(key = "header") { HeaderText(state = state) }
        item(key = "balance") { BalanceCard(state = state) }
        item(key = "actions") { ActionRow(callbacks = callbacks) }
        item(key = "recent-heading") { RecentHeading() }
        if (state.rows.isEmpty()) {
            item(key = "no-rows") { EmptyRowsText() }
        }
        items(visible, key = { it.transactionId }) { row -> RecentRow(detailRow = row) }
        if (state.rows.size > RECENT_LIMIT) {
            item(key = "toggle") {
                ToggleText(expanded = state.expanded, count = state.rows.size, callbacks = callbacks)
            }
        }
    }
}

// S-19: the wallet name and its type badge
@Composable
private fun HeaderText(state: WalletDetailUiState.Success) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = state.item.wallet.profile.name,
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Text(
            text = stringResource(WalletLabels.type(state.item.wallet.kind.type)),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}

// FR-017, S-19: the current balance of the wallet (BR-004)
@Composable
private fun BalanceCard(state: WalletDetailUiState.Success) {
    FintrackCard {
        Text(
            text = stringResource(R.string.wallet_detail_balance_label),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = MoneyFormatter.format(state.item.balance),
            style = FintrackTheme.typography.numberLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

// FR-017: Pakai starts an expense from this wallet; Transfer moves money out of it
@Composable
private fun ActionRow(callbacks: WalletDetailCallbacks) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        FintrackButton(
            text = stringResource(R.string.wallet_detail_use),
            onClick = callbacks.onUse,
            modifier = Modifier.weight(1f),
        )
        FintrackButton(
            text = stringResource(R.string.wallet_detail_transfer),
            onClick = callbacks.onTransfer,
            modifier = Modifier.weight(1f),
        )
    }
}

// S-19: the heading of the recent transactions
@Composable
private fun RecentHeading() {
    Text(
        text = stringResource(R.string.wallet_detail_recent),
        style = FintrackTheme.typography.labelLarge,
        color = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier = Modifier.padding(top = FintrackTheme.spacing.small),
    )
}

// S-19: one recent transaction in the shared row style
@Composable
private fun RecentRow(detailRow: WalletDetailRow) {
    val colors = FintrackTheme.colors
    TransactionRow(
        params =
            TransactionRowParams(
                iconRes = DesignR.drawable.ic_wallet,
                iconColor = colors.brands.primary,
                title = detailRow.title.ifBlank { stringResource(R.string.wallet_detail_untitled) },
                subtitle = detailRow.subtitle,
                amount = detailRow.amount,
                type = detailRow.type,
            ),
        callbacks = TransactionRowCallbacks(onClick = {}),
    )
}

// S-19: "Lihat semua (N)" expands the list in place, "Ringkas" collapses it back to eight rows
@Composable
private fun ToggleText(
    expanded: Boolean,
    count: Int,
    callbacks: WalletDetailCallbacks,
) {
    val label =
        if (expanded) {
            stringResource(R.string.wallet_detail_collapse)
        } else {
            stringResource(R.string.wallet_detail_show_all, count)
        }
    Text(
        text = label,
        style = FintrackTheme.typography.labelLarge,
        color = FintrackTheme.colors.brands.primary,
        modifier =
            Modifier
                .clickable(onClick = callbacks.onToggleExpanded)
                .padding(vertical = FintrackTheme.spacing.small),
    )
}

// S-19: shown when the wallet has no transaction yet
@Composable
private fun EmptyRowsText() {
    Text(
        text = stringResource(R.string.wallet_detail_empty),
        style = FintrackTheme.typography.bodyMedium,
        color = FintrackTheme.colors.texts.onSurfaceVariant,
    )
}

// S-19: shown when the route names a wallet that does not exist
@Composable
private fun MissingText() {
    Text(
        text = stringResource(R.string.wallet_detail_missing),
        style = FintrackTheme.typography.bodyMedium,
        color = FintrackTheme.colors.texts.onSurfaceVariant,
    )
}

private const val RECENT_LIMIT = 8
