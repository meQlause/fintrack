package com.meqlause.fintrack.feature.investment.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenStat
import com.meqlause.fintrack.core.designsystem.component.ScreenStatGrid
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.investment.InvestmentValuation
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.investment.InvestmentDates
import com.meqlause.fintrack.feature.investment.R
import com.meqlause.fintrack.feature.investment.component.GainText

// S-20: "Nilai sekarang" (34 px), the gain line, the last update, then the Modal and Untung card
@Composable
internal fun ValueSummary(valuation: InvestmentValuation) {
    Column(modifier = Modifier.padding(top = FintrackTheme.spacing.small)) {
        ValueLines(valuation)
        ValueStats(valuation)
    }
}

// S-20: the label, the current value, the gain and the last update, one under the other
@Composable
private fun ValueLines(valuation: InvestmentValuation) {
    val colors = FintrackTheme.colors
    Text(
        text = stringResource(R.string.investment_value_label),
        style = FintrackScreenText.label(),
        color = colors.texts.onSurfaceVariant,
    )
    Text(
        text = MoneyFormatter.format(valuation.currentValue),
        style = FintrackScreenText.number(size = 34f, weight = 700),
        color = colors.texts.onSurface,
        maxLines = 1,
        modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
    )
    UpdateLines(valuation)
}

// S-20: the gain line under the value, then the day of the last update, or "belum diperbarui"
@Composable
private fun UpdateLines(valuation: InvestmentValuation) {
    val small = FintrackTheme.spacing.xSmall
    GainText(
        valuation = valuation,
        style = FintrackScreenText.number(size = 14f, weight = 700),
        modifier = Modifier.padding(top = small),
    )
    val updated = valuation.lastValuedAt
    Text(
        text =
            if (updated == null) {
                stringResource(R.string.investment_never_updated)
            } else {
                stringResource(R.string.investment_last_update, InvestmentDates.shortDate(updated))
            },
        style = FintrackScreenText.rowSub(),
        color = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier = Modifier.padding(top = small),
    )
}

// S-20: the Modal and the Untung stats; a loss is shown in the expense colour without a plus
@Composable
private fun ValueStats(valuation: InvestmentValuation) {
    val colors = FintrackTheme.colors
    val isLoss = valuation.gain.rupiah < 0L
    val gainColor = if (isLoss) colors.semantics.expense else colors.semantics.income
    val gainSign = if (isLoss) "" else "+"
    ScreenStatGrid(
        modifier = Modifier.padding(top = FintrackTheme.spacing.large),
        stats =
            listOf(
                ScreenStat(
                    label = stringResource(R.string.investment_modal_label),
                    value = MoneyFormatter.format(valuation.modal),
                ),
                ScreenStat(
                    label = stringResource(R.string.investment_gain_label),
                    value = gainSign + MoneyFormatter.formatCompact(valuation.gain),
                    color = gainColor,
                ),
            ),
    )
}
