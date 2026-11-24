package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R

private const val IDLE_ALPHA = 0.14f
private const val FUTURE_ALPHA = 0.06f
private const val TICK_STEP = 7
private const val TICK_COUNT = 4
private const val BAR_RADIUS_RATIO = 0.75f
private val TICK_DAYS = List(TICK_COUNT) { 1 + it * TICK_STEP }

// FR-061, S-08, DOC-08: the Harian card, one rounded bar per day scaled to the busiest day, with the daily average
@Composable
internal fun StatisticsDailyBars(
    statistics: PeriodStatistics,
    todayIndex: Int?,
) {
    val days = statistics.dailyTotals.size.coerceAtLeast(1)
    FintrackCard(
        modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.small + FintrackTheme.spacing.xxSmall),
    ) {
        DailyHeader(statistics = statistics, days = days)
        DailyCanvas(statistics = statistics, todayIndex = todayIndex)
        DayTicks(lastDay = days)
    }
}

// FR-061: the card title and the average per day
@Composable
private fun DailyHeader(
    statistics: PeriodStatistics,
    days: Int,
) {
    val colors = FintrackTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.history_stats_daily_title),
            style = FintrackTheme.typography.caption,
            color = colors.texts.onSurfaceVariant,
        )
        Text(
            text =
                stringResource(
                    R.string.history_stats_average,
                    MoneyFormatter.formatShort(Money(statistics.total.rupiah / days)),
                ),
            style = FintrackTheme.typography.captionWide,
            color = colors.texts.onSurface,
        )
    }
}

// FR-061: one bar per day; today is drawn in the primary colour, later days fainter, the others faint
@Composable
private fun DailyCanvas(
    statistics: PeriodStatistics,
    todayIndex: Int?,
) {
    val colors = FintrackTheme.colors
    val barGap = FintrackTheme.sizes.barGap
    val radius = FintrackTheme.spacing.xSmall * BAR_RADIUS_RATIO
    Canvas(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = FintrackTheme.spacing.stack)
                .height(FintrackTheme.sizes.statisticsBarAreaHeight),
    ) {
        val barColors =
            BarColors(
                filled = colors.brands.primary,
                idle = colors.texts.onSurface.copy(alpha = IDLE_ALPHA),
                future = colors.texts.onSurface.copy(alpha = FUTURE_ALPHA),
            )
        val amounts = statistics.dailyTotals.map { it.rupiah }
        drawDailyBars(amounts, DayMarks(todayIndex, barColors), barGap.toPx(), radius.toPx())
    }
}

// DOC-08: the day numbers 1, 8, 15, 22 and the last day under the bars
@Composable
private fun DayTicks(lastDay: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        (TICK_DAYS + lastDay).forEach { day ->
            Text(
                text = day.toString(),
                style = FintrackTheme.typography.micro,
                color = FintrackTheme.colors.texts.faint,
            )
        }
    }
}

private fun DrawScope.drawDailyBars(
    amounts: List<Long>,
    marks: DayMarks,
    spacing: Float,
    radius: Float,
) {
    val peak = (amounts.maxOrNull() ?: 0L).coerceAtLeast(1L)
    val count = amounts.size.coerceAtLeast(1)
    val barWidth = (size.width - spacing * (count - 1)) / count
    amounts.forEachIndexed { index, rupiah ->
        val height = if (rupiah == 0L) radius else size.height * (rupiah.toFloat() / peak)
        drawRoundRect(
            color = marks.colorOf(index),
            topLeft = Offset(index * (barWidth + spacing), size.height - height),
            size = Size(barWidth, height),
            cornerRadius = CornerRadius(radius),
        )
    }
}

// DOC-08: the colour of today's bar, of the bars before it and of the bars after it
private data class BarColors(
    val filled: Color,
    val idle: Color,
    val future: Color,
)

// FR-061: the bar of today is filled; without a today every bar is idle, as in the prototype
private data class DayMarks(
    val todayIndex: Int?,
    val colors: BarColors,
) {
    fun colorOf(index: Int): Color =
        when {
            todayIndex == null || index < todayIndex -> colors.idle
            index == todayIndex -> colors.filled
            else -> colors.future
        }
}
