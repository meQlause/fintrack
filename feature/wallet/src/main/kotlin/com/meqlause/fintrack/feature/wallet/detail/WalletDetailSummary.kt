package com.meqlause.fintrack.feature.wallet.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenStat
import com.meqlause.fintrack.core.designsystem.component.ScreenStatGrid
import com.meqlause.fintrack.core.designsystem.icon.IconBox
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.icon.TintedIconBox
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.wallet.R
import com.meqlause.fintrack.feature.wallet.WalletItem

// FR-017, S-19: the 52 px tinted icon beside "Saldo" and the 32 px balance (BR-004)
@Composable
internal fun WalletDetailBalance(
    item: WalletItem,
    modifier: Modifier = Modifier,
) {
    val appearance = item.wallet.profile.appearance
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.rowPaddingHorizontal),
    ) {
        TintedIconBox(
            look = IconLook(iconKey = appearance.iconKey, colorKey = appearance.colorKey),
            tile =
                IconBox(
                    size = FintrackScreenSizes.heroIcon,
                    glyph = FintrackScreenSizes.heroGlyph,
                    shape = RoundedCornerShape(FintrackScreenSizes.heroCorner),
                ),
        )
        BalanceTexts(balance = item.balance, modifier = Modifier.weight(1f))
    }
}

// FR-017: the "Saldo" label over the 32 px balance
@Composable
private fun BalanceTexts(
    balance: Money,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.wallet_detail_balance_label),
            style = FintrackScreenText.label(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = MoneyFormatter.format(balance),
            style = FintrackScreenText.number(size = 32f, weight = 700),
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

// FR-017, S-19: this month's Masuk, Keluar and number of transactions in one three-column card
@Composable
internal fun WalletDetailStatsCard(
    stats: WalletDetailStats,
    modifier: Modifier = Modifier,
) {
    val semantics = FintrackTheme.colors.semantics
    ScreenStatGrid(
        modifier = modifier.padding(top = FintrackTheme.spacing.large),
        stats =
            listOf(
                ScreenStat(
                    label = stringResource(R.string.wallet_detail_in),
                    value = "+" + MoneyFormatter.formatCompact(stats.inflow),
                    color = semantics.income,
                ),
                ScreenStat(
                    label = stringResource(R.string.wallet_detail_out),
                    value = "−" + MoneyFormatter.formatCompact(stats.outflow),
                    color = semantics.expense,
                ),
                ScreenStat(
                    label = stringResource(R.string.wallet_detail_count),
                    value = stats.count.toString(),
                ),
            ),
    )
}
