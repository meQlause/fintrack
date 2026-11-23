package com.meqlause.fintrack.feature.investment.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.component.ProgressBar
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.investment.InvestmentPortfolio
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.investment.InvestmentLabels
import com.meqlause.fintrack.feature.investment.R

// FR-173: the total value and gain over every account, with the allocation by kind
@Composable
internal fun PortfolioCard(
    portfolio: InvestmentPortfolio,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    val typography = FintrackTheme.typography
    val total = portfolio.total
    FintrackCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.investment_total_title),
            style = typography.labelMedium,
            color = colors.texts.onSurfaceVariant,
        )
        Text(
            text = MoneyFormatter.format(total.currentValue),
            style = typography.numberHero,
            color = colors.texts.onSurface,
            maxLines = 1,
        )
        GainText(valuation = total, style = typography.bodyMedium)
        Spacer(modifier = Modifier.height(FintrackTheme.spacing.medium))
        Text(
            text = stringResource(R.string.investment_allocation_title),
            style = typography.labelLarge,
            color = colors.texts.onSurface,
        )
        portfolio.allocation.forEach { (kind, value) ->
            AllocationRow(kind = kind, value = value, total = total.currentValue)
        }
    }
}

// FR-173: one kind of investment as its share of the total value
@Composable
private fun AllocationRow(
    kind: InvestKind,
    value: Money,
    total: Money,
) {
    val colors = FintrackTheme.colors
    val typography = FintrackTheme.typography
    val fraction = if (total.rupiah <= 0L) 0f else value.rupiah.toFloat() / total.rupiah.toFloat()
    Column(modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.xSmall)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(InvestmentLabels.kindRes(kind)),
                modifier = Modifier.weight(1f),
                style = typography.bodyMedium,
                color = colors.texts.onSurface,
            )
            Text(
                text = MoneyFormatter.format(value),
                style = typography.bodyMedium,
                color = colors.texts.onSurfaceVariant,
                maxLines = 1,
            )
        }
        ProgressBar(
            fraction = fraction,
            color = colors.brands.primary,
            modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
        )
    }
}
