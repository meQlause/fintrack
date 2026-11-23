package com.meqlause.fintrack.feature.investment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.investment.InvestmentPortfolio
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.investment.InvestmentLabels
import com.meqlause.fintrack.feature.investment.R

// FR-173, S-08: "Total nilai", the total (30 px) and the gain line, 18 px under the header
@Composable
internal fun PortfolioHero(
    portfolio: InvestmentPortfolio,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(top = FintrackTheme.spacing.large + FintrackTheme.spacing.xxSmall)) {
        Text(
            text = stringResource(R.string.investment_total_title),
            style = FintrackScreenText.label(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = MoneyFormatter.format(portfolio.total.currentValue),
            style = FintrackScreenText.netWorth(),
            color = FintrackTheme.colors.texts.onSurface,
            modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
        )
        GainText(
            valuation = portfolio.total,
            style = FintrackScreenText.number(size = 14f, weight = 700),
            modifier = Modifier.padding(top = FintrackTheme.spacing.xSmall),
        )
    }
}

// FR-173: the allocation card, one segment per kind in the bar and a legend row with value and share
@Composable
internal fun AllocationCard(
    portfolio: InvestmentPortfolio,
    modifier: Modifier = Modifier,
) {
    val total = portfolio.total
    ScreenCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(FintrackTheme.spacing.large)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = stringResource(R.string.investment_allocation_title),
                    style = FintrackScreenText.label(),
                    color = FintrackTheme.colors.texts.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.investment_modal_total, MoneyFormatter.format(total.modal)),
                    style = FintrackScreenText.number(size = 12f, weight = 400),
                    color = FintrackTheme.colors.texts.onSurfaceVariant,
                )
            }
            AllocationBar(portfolio = portfolio)
            portfolio.allocation.forEach { (kind, value) ->
                AllocationRow(kind = kind, value = value, total = total.currentValue)
            }
        }
    }
}

@Composable
private fun AllocationBar(portfolio: InvestmentPortfolio) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = FintrackTheme.spacing.medium)
                .height(FintrackScreenSizes.allocBar),
        horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.allocGap),
    ) {
        portfolio.allocation.filter { it.value.rupiah > 0L }.forEach { (kind, value) ->
            Row(
                modifier =
                    Modifier
                        .weight(value.rupiah.toFloat())
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(FintrackScreenSizes.allocCorner))
                        .background(FintrackTheme.colors.categoryColor(kindColorKey(kind))),
            ) {}
        }
    }
}

// FR-173: dot and kind name, the compact value and the share of the total
@Composable
private fun AllocationRow(
    kind: InvestKind,
    value: Money,
    total: Money,
) {
    val share = if (total.rupiah <= 0L) 0 else ((value.rupiah * PERCENT + total.rupiah / 2) / total.rupiah).toInt()
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = FintrackScreenSizes.legendRowTop),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        Row(
            modifier =
                Modifier
                    .size(FintrackScreenSizes.legendDot)
                    .clip(CircleShape)
                    .background(FintrackTheme.colors.categoryColor(kindColorKey(kind))),
        ) {}
        Text(
            text = stringResource(InvestmentLabels.kindRes(kind)),
            style = FintrackScreenText.legend(weight = 500),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "Rp" + MoneyFormatter.formatCompact(value),
            style = FintrackScreenText.legendNumber(),
            color = FintrackTheme.colors.texts.onSurface,
        )
        Text(
            text = stringResource(R.string.investment_percent_share, share),
            style = FintrackScreenText.number(size = 12.5f, weight = 400),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.width(FintrackScreenSizes.allocShare),
        )
    }
}

private const val PERCENT = 100L

// S-08: the palette colour of each investment kind in the allocation bar and legend
internal fun kindColorKey(kind: InvestKind): String =
    when (kind) {
        InvestKind.GOLD -> "amber"
        InvestKind.MUTUAL_FUND -> "green"
        InvestKind.STOCK -> "blue"
        InvestKind.DEPOSIT -> "cyan"
        InvestKind.CRYPTO -> "purple"
        InvestKind.OTHER -> "gray"
    }
