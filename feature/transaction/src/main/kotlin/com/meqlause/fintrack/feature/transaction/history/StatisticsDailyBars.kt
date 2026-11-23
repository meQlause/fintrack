package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics
import com.meqlause.fintrack.feature.transaction.R

private const val BAR_WIDTH_RATIO = 0.6f

// FR-061, S-08: one bar per day of the period, scaled to the busiest day; days without money stay as a hairline
@Composable
internal fun StatisticsDailyBars(statistics: PeriodStatistics) {
    val colors = FintrackTheme.colors
    val hairline = FintrackTheme.sizes.hairline
    Column(modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.xLarge)) {
        Text(
            text = stringResource(R.string.history_stats_daily_title),
            style = FintrackTheme.typography.labelLarge,
            color = colors.texts.onSurface,
        )
        Canvas(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = FintrackTheme.spacing.small)
                    .height(FintrackTheme.sizes.shortcutTileWidth),
        ) {
            drawDailyBars(
                amounts = statistics.dailyTotals.map { it.rupiah },
                filled = colors.brands.primary,
                idle = colors.surfaces.outline,
                hairline = hairline,
            )
        }
    }
}

private fun DrawScope.drawDailyBars(
    amounts: List<Long>,
    filled: Color,
    idle: Color,
    hairline: Dp,
) {
    val peak = (amounts.maxOrNull() ?: 0L).coerceAtLeast(1L)
    val slot = size.width / amounts.size.coerceAtLeast(1)
    val barWidth = slot * BAR_WIDTH_RATIO
    val idleHeight = hairline.toPx()
    amounts.forEachIndexed { index, rupiah ->
        val isIdle = rupiah == 0L
        val height = if (isIdle) idleHeight else size.height * (rupiah.toFloat() / peak)
        drawRect(
            color = if (isIdle) idle else filled,
            topLeft = Offset(index * slot + (slot - barWidth) / 2f, size.height - height),
            size = Size(barWidth, height),
        )
    }
}
