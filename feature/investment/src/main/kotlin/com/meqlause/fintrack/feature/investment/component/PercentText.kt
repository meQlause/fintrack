package com.meqlause.fintrack.feature.investment.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.investment.InvestmentValuation
import com.meqlause.fintrack.feature.investment.InvestmentLabels

// FR-173: "+4,0%" in 11.5 px bold, green for a gain and red for a loss, under an account value
@Composable
internal fun PercentText(
    valuation: InvestmentValuation,
    modifier: Modifier = Modifier,
) {
    val tenths = valuation.gainTenthPercent
    val sign = if (tenths < 0L) "" else "+"
    val semantics = FintrackTheme.colors.semantics
    Text(
        text = "$sign${InvestmentLabels.percentText(tenths)}%",
        modifier = modifier,
        style = FintrackScreenText.number(size = 11.5f, weight = 700),
        color = if (tenths < 0L) semantics.expense else semantics.income,
        maxLines = 1,
    )
}
