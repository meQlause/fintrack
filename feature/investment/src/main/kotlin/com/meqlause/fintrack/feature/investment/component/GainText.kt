package com.meqlause.fintrack.feature.investment.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.investment.InvestmentValuation
import com.meqlause.fintrack.feature.investment.InvestmentLabels

// FR-173: the gain line, in the income colour when it is not a loss and the expense colour when it is
@Composable
internal fun GainText(
    valuation: InvestmentValuation,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    val semantics = FintrackTheme.colors.semantics
    val color = if (valuation.gain.rupiah < 0L) semantics.expense else semantics.income
    Text(
        text = InvestmentLabels.gainLine(valuation),
        modifier = modifier,
        style = style,
        color = color,
        maxLines = 1,
    )
}
