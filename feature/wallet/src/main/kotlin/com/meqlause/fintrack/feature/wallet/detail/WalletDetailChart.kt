package com.meqlause.fintrack.feature.wallet.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.wallet.R

// FR-017, S-19: "Keluar per hari" card, one bar per day of the month; days after today are drawn in the outline colour
@Composable
internal fun WalletDetailChart(
    stats: WalletDetailStats,
    colorKey: String,
    modifier: Modifier = Modifier,
) {
    val average = if (stats.today > 0) stats.outflow.rupiah / stats.today else 0L
    ScreenCard(modifier = modifier) {
        val inset = PaddingValues(horizontal = FintrackTheme.spacing.large, vertical = FintrackScreenSizes.screenTop)
        Column(modifier = Modifier.padding(inset)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = stringResource(R.string.wallet_detail_chart_title, stats.monthLabel),
                    style = FintrackScreenText.label(),
                    color = FintrackTheme.colors.texts.onSurfaceVariant,
                )
                Text(
                    text =
                        stringResource(
                            R.string.wallet_detail_chart_average,
                            "Rp" + MoneyFormatter.formatCompact(Money(average)),
                        ),
                    style = FintrackScreenText.number(size = 12f, weight = 600),
                    color = FintrackTheme.colors.texts.onSurface,
                )
            }
            Bars(stats = stats, colorKey = colorKey, modifier = Modifier.padding(top = FintrackTheme.spacing.medium))
        }
    }
}

@Composable
private fun Bars(
    stats: WalletDetailStats,
    colorKey: String,
    modifier: Modifier,
) {
    val peak = (stats.perDay.maxOrNull() ?: 0L).coerceAtLeast(1L)
    Row(
        modifier = modifier.fillMaxWidth().height(FintrackScreenSizes.chartHeight),
        horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.chartGap),
        verticalAlignment = Alignment.Bottom,
    ) {
        stats.perDay.forEachIndexed { index, value ->
            val day = index + 1
            val color =
                when {
                    day > stats.today -> FintrackTheme.colors.surfaces.outline
                    day == stats.today -> FintrackTheme.colors.brands.primary
                    else -> FintrackTheme.colors.categoryColor(colorKey)
                }
            Bar(value = value, peak = peak, color = color, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun Bar(
    value: Long,
    peak: Long,
    color: Color,
    modifier: Modifier,
) {
    val full = FintrackScreenSizes.chartHeight
    val height: Dp =
        if (value == 0L) {
            FintrackScreenSizes.chartBarEmpty
        } else {
            maxOf(FintrackScreenSizes.chartBarMin, full * (value.toFloat() / peak))
        }
    Box(
        modifier =
            modifier
                .height(height)
                .clip(RoundedCornerShape(FintrackScreenSizes.chartBarCorner))
                .background(color),
    )
}
