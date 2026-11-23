package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.AmountSign
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-040, S-03: the hero card: Saldo with count-up, privacy toggle, summary pill, wallet share bar and wallet chips
@Composable
internal fun HomeHero(
    state: HomeUiState.Success,
    callbacks: HomeCallbacks,
    modifier: Modifier = Modifier,
) {
    FintrackCard(modifier = modifier.fillMaxWidth()) {
        HomeHeroLabel(hidden = state.hidden, onToggle = callbacks.onToggleHidden)
        Box(modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall)) {
            if (state.hidden) {
                HomeAmountText(money = state.total, hidden = true, style = FintrackTheme.typography.numberHero)
            } else {
                CountUpMoney(money = state.total)
            }
        }
        HomeNetPill(
            net = state.period.net,
            hidden = state.hidden,
            onClick = callbacks.onOpenSummary,
            modifier = Modifier.padding(top = FintrackTheme.spacing.small),
        )
        HomeWalletShareBar(wallets = state.wallets, modifier = Modifier.padding(top = FintrackTheme.spacing.medium))
        HomeWalletRow(
            wallets = state.wallets,
            hidden = state.hidden,
            onOpenWallet = callbacks.onOpenWallet,
            modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
        )
    }
}

// FR-040: the Saldo label and the eye button that hides or shows every amount on Beranda
@Composable
private fun HomeHeroLabel(
    hidden: Boolean,
    onToggle: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.home_balance_label),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Box(
            modifier =
                Modifier
                    .size(FintrackTheme.sizes.touchTarget)
                    .clip(FintrackTheme.shapes.full)
                    .clickable(role = Role.Button, onClick = onToggle),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_eye),
                contentDescription =
                    stringResource(if (hidden) R.string.home_show_amounts else R.string.home_hide_amounts),
                colorFilter = ColorFilter.tint(FintrackTheme.colors.texts.onSurfaceVariant),
                modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
            )
        }
    }
}

// FR-041: the period net as a pill; tapping it opens the Ringkasan drawer
@Composable
private fun HomeNetPill(
    net: Money,
    hidden: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .clip(FintrackTheme.shapes.full)
                .background(FintrackTheme.colors.surfaces.surfaceVariant)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = FintrackTheme.spacing.medium, vertical = FintrackTheme.spacing.xSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall),
    ) {
        Text(
            text = netLabel(net = net, hidden = hidden),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
        )
        Image(
            painter = painterResource(DesignR.drawable.ic_chevron_right),
            contentDescription = stringResource(R.string.home_summary_open),
            colorFilter = ColorFilter.tint(FintrackTheme.colors.texts.onSurfaceVariant),
            modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
        )
    }
}

@Composable
private fun netLabel(
    net: Money,
    hidden: Boolean,
): String =
    if (hidden) {
        stringResource(R.string.home_amount_hidden)
    } else {
        stringResource(R.string.home_net_suffix, MoneyFormatter.formatSigned(net, AmountSign.PLUS))
    }

// FR-040: each wallet's share of the positive balances as one slim bar; nothing is drawn when no wallet is positive
@Composable
private fun HomeWalletShareBar(
    wallets: List<HomeWallet>,
    modifier: Modifier = Modifier,
) {
    val positive = wallets.filter { it.balance.rupiah > 0L }
    if (positive.isEmpty()) return
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(FintrackTheme.spacing.xSmall)
                .clip(FintrackTheme.shapes.full),
    ) {
        positive.forEachIndexed { index, wallet ->
            Box(
                modifier =
                    Modifier
                        .weight(wallet.balance.rupiah.toFloat())
                        .fillMaxHeight()
                        .background(homeSegmentColor(FintrackTheme.colors, index)),
            )
        }
    }
}
