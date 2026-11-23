package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-040, S-03: the hero block as the mockup draws it, flat on the page: Saldo, balance, summary pill, bar, tiles
@Composable
internal fun HomeHero(
    state: HomeUiState.Success,
    callbacks: HomeCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HeroSaldo(state = state, callbacks = callbacks)
        HomeWalletRow(
            wallets = state.wallets,
            hidden = state.hidden,
            callbacks = callbacks,
            modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
        )
    }
}

// FR-040: the Saldo label, the balance, the summary pill and the wallet share bar, inset by the gutter
@Composable
private fun HeroSaldo(
    state: HomeUiState.Success,
    callbacks: HomeCallbacks,
) {
    Column(modifier = Modifier.padding(horizontal = FintrackTheme.spacing.gutter)) {
        HomeHeroLabel(hidden = state.hidden, onToggle = callbacks.onToggleHidden)
        Box(modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall)) {
            if (state.hidden) {
                HeroAmount(formatted = stringResource(R.string.home_amount_hidden))
            } else {
                CountUpMoney(money = state.total)
            }
        }
        HomeNetPill(
            netAmount = state.period.netTotal,
            hidden = state.hidden,
            onClick = callbacks.onOpenSummary,
            modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
        )
        HomeWalletShareBar(
            wallets = state.wallets,
            modifier = Modifier.padding(top = FintrackTheme.spacing.section),
        )
    }
}

// FR-040: the Saldo label and the eye button that hides or shows every amount on Beranda
@Composable
private fun HomeHeroLabel(
    hidden: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall),
    ) {
        Text(
            text = stringResource(R.string.home_balance_label),
            style = FintrackTheme.typography.caption,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Box(
            modifier =
                Modifier
                    .size(FintrackTheme.sizes.eyeButton)
                    .clip(FintrackTheme.shapes.full)
                    .clickable(role = Role.Button, onClick = onToggle),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_eye),
                contentDescription =
                    stringResource(if (hidden) R.string.home_show_amounts else R.string.home_hide_amounts),
                colorFilter = ColorFilter.tint(FintrackTheme.colors.texts.onSurfaceVariant),
                modifier = Modifier.size(FintrackTheme.sizes.shortcutGlyph),
            )
        }
    }
}

// FR-041: the period net as a pill; tapping it opens the Ringkasan drawer
@Composable
private fun HomeNetPill(
    netAmount: Money,
    hidden: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = FintrackTheme.shapes.full
    Row(
        modifier =
            modifier
                .height(FintrackTheme.sizes.summaryPillHeight)
                .clip(shape)
                .background(FintrackTheme.colors.surfaces.surface)
                .border(FintrackTheme.sizes.hairline, FintrackTheme.colors.surfaces.outline, shape)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(start = FintrackTheme.spacing.medium, end = FintrackTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall),
    ) {
        Text(
            text = netLabel(netAmount = netAmount, hidden = hidden),
            style = FintrackTheme.typography.captionWide,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            maxLines = 1,
        )
        Image(
            painter = painterResource(DesignR.drawable.ic_chevron_right),
            contentDescription = stringResource(R.string.home_summary_open),
            colorFilter = ColorFilter.tint(FintrackTheme.colors.texts.onSurfaceVariant),
            modifier = Modifier.size(FintrackTheme.sizes.shortcutGlyph),
        )
    }
}

// DOC-08: the net amount is coloured by its sign and the words "bulan ini" follow in the muted pill colour
@Composable
private fun netLabel(
    netAmount: Money,
    hidden: Boolean,
) = buildAnnotatedString {
    val colors = FintrackTheme.colors
    val tone: Color = if (netAmount.rupiah < 0L) colors.semantics.expense else colors.semantics.income
    withStyle(SpanStyle(color = tone)) {
        append(
            if (hidden) stringResource(R.string.home_amount_hidden) else MoneyFormatter.formatCompactSigned(netAmount),
        )
    }
    append(" ")
    append(stringResource(R.string.home_net_words))
}
