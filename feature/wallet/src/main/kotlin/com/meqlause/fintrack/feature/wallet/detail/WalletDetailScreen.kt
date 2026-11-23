package com.meqlause.fintrack.feature.wallet.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.BackAction
import com.meqlause.fintrack.core.designsystem.component.HeaderTitle
import com.meqlause.fintrack.core.designsystem.component.ScreenAction
import com.meqlause.fintrack.core.designsystem.component.ScreenActionButton
import com.meqlause.fintrack.core.designsystem.component.ScreenBackHeader
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.transaction.TransactionRowGroup
import com.meqlause.fintrack.core.ui.transaction.TransactionRowParams
import com.meqlause.fintrack.feature.wallet.R
import com.meqlause.fintrack.feature.wallet.WalletLabels
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-017, S-19: stateless wallet detail; header, balance, stats, Pakai and Transfer, chart, then recent transactions
@Composable
internal fun WalletDetailScreen(
    state: WalletDetailUiState,
    callbacks: WalletDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(FintrackTheme.colors.surfaces.background)) {
        val success = state as? WalletDetailUiState.Success
        ScreenBackHeader(
            title =
                HeaderTitle(
                    text =
                        success
                            ?.item
                            ?.wallet
                            ?.profile
                            ?.name
                            .orEmpty(),
                    badge = success?.let { stringResource(WalletLabels.type(it.item.wallet.kind.type)) },
                ),
            back = BackAction(label = stringResource(R.string.wallet_detail_back), onBack = callbacks.onBack),
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
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding =
            PaddingValues(
                start = FintrackScreenSizes.screenGutter,
                end = FintrackScreenSizes.screenGutter,
                top = FintrackTheme.spacing.xSmall + FintrackTheme.spacing.xxSmall,
                bottom = FintrackTheme.spacing.xxLarge,
            ),
    ) {
        item(key = "balance") {
            WalletDetailBalance(item = state.item, modifier = Modifier.padding(top = FintrackTheme.spacing.small))
        }
        item(key = "stats") { WalletDetailStatsCard(stats = state.stats) }
        item(key = "actions") { ActionRow(callbacks = callbacks) }
        item(key = "chart") {
            WalletDetailChart(
                stats = state.stats,
                colorKey = state.item.wallet.profile.appearance.colorKey,
                modifier = Modifier.padding(top = FintrackTheme.spacing.small + FintrackTheme.spacing.xxSmall),
            )
        }
        item(key = "recent-heading") { RecentHeading(state = state, callbacks = callbacks) }
        item(key = "recent") { RecentCard(rows = visible) }
    }
}

// FR-017: Pakai starts an expense from this wallet; Transfer moves money out of it
@Composable
private fun ActionRow(callbacks: WalletDetailCallbacks) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.small + FintrackTheme.spacing.xxSmall),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        ScreenActionButton(
            action =
                ScreenAction(
                    text = stringResource(R.string.wallet_detail_use),
                    iconRes = DesignR.drawable.ic_up,
                    tint = FintrackTheme.colors.semantics.expense,
                ),
            onClick = callbacks.onUse,
            modifier = Modifier.weight(1f),
        )
        ScreenActionButton(
            action =
                ScreenAction(
                    text = stringResource(R.string.wallet_detail_transfer),
                    iconRes = DesignR.drawable.ic_arrow_r,
                    tint = FintrackTheme.colors.semantics.transfer,
                ),
            onClick = callbacks.onTransfer,
            modifier = Modifier.weight(1f),
        )
    }
}

// S-19: "Terbaru" with "Lihat semua (N)" or "Ringkas" at the right, 20 px below the chart
@Composable
private fun RecentHeading(
    state: WalletDetailUiState.Success,
    callbacks: WalletDetailCallbacks,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = FintrackTheme.spacing.xxSmall,
                    end = FintrackTheme.spacing.xxSmall,
                    top = FintrackScreenSizes.groupGap,
                    bottom = FintrackScreenSizes.groupLabelBottom,
                ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.wallet_detail_recent),
            style = FintrackScreenText.label(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        if (state.rows.size > RECENT_LIMIT) {
            val label =
                if (state.expanded) {
                    stringResource(R.string.wallet_detail_collapse)
                } else {
                    stringResource(R.string.wallet_detail_show_all, state.rows.size)
                }
            Text(
                text = label,
                style = FintrackScreenText.style(size = 12.5f, weight = 700),
                color = FintrackTheme.colors.brands.primary,
                modifier = Modifier.clickable(onClick = callbacks.onToggleExpanded),
            )
        }
    }
}

// S-19: the recent transactions in one card, or "Belum ada transaksi" when the wallet has none
@Composable
private fun RecentCard(rows: List<WalletDetailRow>) {
    if (rows.isEmpty()) {
        EmptyRowsText()
        return
    }
    val untitled = stringResource(R.string.wallet_detail_untitled)
    TransactionRowGroup(
        rows =
            rows.map { row ->
                TransactionRowParams(
                    look = IconLook(iconKey = row.appearance.iconKey, colorKey = row.appearance.colorKey),
                    title = row.title.ifBlank { untitled },
                    subtitle = row.subtitle,
                    amount = row.amount,
                    type = row.type,
                )
            },
        onClick = {},
    )
}

// S-19: shown when the wallet has no transaction yet
@Composable
private fun EmptyRowsText() {
    Text(
        text = stringResource(R.string.wallet_detail_empty),
        style = FintrackScreenText.style(size = 13f, weight = 400),
        color = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth().padding(FintrackScreenSizes.screenGutter),
    )
}

// S-19: shown when the route names a wallet that does not exist
@Composable
private fun MissingText() {
    Text(
        text = stringResource(R.string.wallet_detail_missing),
        style = FintrackTheme.typography.bodyMedium,
        color = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier = Modifier.padding(FintrackScreenSizes.screenGutter),
    )
}

private const val RECENT_LIMIT = 8
