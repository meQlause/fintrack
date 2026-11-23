package com.meqlause.fintrack.feature.wallet.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.AmountSign
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.wallet.R

// BR-053, FR-161, S-09: Kekayaan bersih with the Aset vs Utang bar and the two columns of parts
@Composable
internal fun WalletNetWorthCard(netWorth: NetWorth) {
    FintrackCard {
        Text(
            text = stringResource(R.string.wallet_networth_label),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = MoneyFormatter.format(netWorth.total),
            style = FintrackTheme.typography.numberLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
        AssetDebtBar(assets = netWorth.assets, debts = netWorth.debts)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.small),
            horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
        ) {
            BreakdownColumn(
                title = stringResource(R.string.wallet_networth_assets),
                total = MoneyFormatter.format(netWorth.assets),
                color = FintrackTheme.colors.semantics.income,
                rows =
                    listOf(
                        stringResource(R.string.wallet_networth_saldo) to MoneyFormatter.format(netWorth.saldo),
                        stringResource(R.string.wallet_networth_invest) to MoneyFormatter.format(netWorth.investasi),
                        stringResource(R.string.wallet_networth_receivable) to MoneyFormatter.format(netWorth.piutang),
                    ),
                modifier = Modifier.weight(1f),
            )
            BreakdownColumn(
                title = stringResource(R.string.wallet_networth_debts),
                total = MoneyFormatter.formatSigned(netWorth.debts, AmountSign.MINUS),
                color = FintrackTheme.colors.semantics.expense,
                rows =
                    listOf(
                        stringResource(R.string.wallet_networth_card_debt) to
                            MoneyFormatter.formatSigned(netWorth.utangKartu, AmountSign.MINUS),
                        stringResource(R.string.wallet_networth_debt) to
                            MoneyFormatter.formatSigned(netWorth.hutang, AmountSign.MINUS),
                    ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// BR-053: the share of assets against debts; an empty pair shows a flat neutral bar
@Composable
private fun AssetDebtBar(
    assets: Money,
    debts: Money,
) {
    val assetPart = assets.rupiah.coerceAtLeast(0L).toFloat()
    val debtPart = debts.rupiah.coerceAtLeast(0L).toFloat()
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(FintrackTheme.sizes.netWorthBarHeight)
                .clip(FintrackTheme.shapes.full),
    ) {
        if (assetPart + debtPart == 0f) {
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight().background(FintrackTheme.colors.surfaces.outline),
            )
        } else {
            if (assetPart > 0f) {
                Box(
                    modifier =
                        Modifier.weight(assetPart).fillMaxHeight().background(FintrackTheme.colors.semantics.income),
                )
            }
            if (debtPart > 0f) {
                Box(
                    modifier =
                        Modifier.weight(debtPart).fillMaxHeight().background(FintrackTheme.colors.semantics.expense),
                )
            }
        }
    }
}

// BR-053: one side of the bar with its total and its parts
@Composable
private fun BreakdownColumn(
    title: String,
    total: String,
    color: Color,
    rows: List<Pair<String, String>>,
    modifier: Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(text = total, style = FintrackTheme.typography.titleMedium, color = color)
        rows.forEach { (label, value) ->
            Column(modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.small)) {
                Text(
                    text = label,
                    style = FintrackTheme.typography.labelMedium,
                    color = FintrackTheme.colors.texts.onSurfaceVariant,
                    maxLines = 1,
                )
                Text(
                    text = value,
                    style = FintrackTheme.typography.bodyMedium,
                    color = FintrackTheme.colors.texts.onSurface,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}
