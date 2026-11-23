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
    val colors = FintrackTheme.colors
    val small = FintrackTheme.spacing.xSmall
    Column(modifier = Modifier.padding(top = FintrackTheme.spacing.small)) {
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
            color = colors.texts.onSurfaceVariant,
            modifier = Modifier.padding(top = small),
        )
        val gainColor = if (valuation.gain.rupiah < 0L) colors.semantics.expense else colors.semantics.income
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
                        value = (if (valuation.gain.rupiah < 0L) "" else "+") + MoneyFormatter.formatCompact(valuation.gain),
                        color = gainColor,
                    ),
                ),
        )
    }
}
