package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics

private val DONUT_SIZE = 184.dp
private val DONUT_STROKE = 26.dp
private const val FULL_TURN = 360f
private const val TOP_OF_CIRCLE = -90f

// FR-060, S-08: one arc per category, sized by its amount; the first category starts at the top
@Composable
internal fun StatisticsDonut(
    statistics: PeriodStatistics,
    modifier: Modifier = Modifier,
) {
    val palette = statisticsPalette(FintrackTheme.colors)
    Canvas(modifier = modifier.size(DONUT_SIZE)) {
        val strokePx = DONUT_STROKE.toPx()
        val inset = strokePx / 2f
        val arcSize = Size(size.width - strokePx, size.height - strokePx)
        var start = TOP_OF_CIRCLE
        statistics.categories.forEachIndexed { index, change ->
            val sweep = FULL_TURN * change.amount.rupiah / statistics.total.rupiah.toFloat()
            drawArc(
                color = palette[index % palette.size],
                startAngle = start,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokePx),
            )
            start += sweep
        }
    }
}
