package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.CategoryId

private const val FULL_TURN = 360f
private const val TOP_OF_CIRCLE = -90f
private const val ARC_GAP_DEGREES = 2f
private const val MIN_SWEEP = 0.1f

// FR-060, S-08, DOC-08: one rounded arc per category on a track ring; the first category starts at the top
@Composable
internal fun StatisticsDonut(
    statistics: PeriodStatistics,
    appearances: Map<CategoryId, Appearance>,
    modifier: Modifier = Modifier,
) {
    val colors = statistics.categories.map { statisticsColorOf(it, appearances) }
    val track = FintrackTheme.colors.surfaces.surfaceVariant
    val donutStroke = FintrackTheme.sizes.donutStrokeWidth
    Canvas(modifier = modifier.size(FintrackTheme.sizes.donutSize)) {
        val strokePx = donutStroke.toPx()
        val inset = strokePx / 2f
        val arcSize = Size(size.width - strokePx, size.height - strokePx)
        val capDegrees = Math.toDegrees((strokePx / 2f / (arcSize.width / 2f)).toDouble()).toFloat()
        drawArc(track, 0f, FULL_TURN, false, Offset(inset, inset), arcSize, style = Stroke(width = strokePx))
        var start = TOP_OF_CIRCLE
        statistics.categories.forEachIndexed { index, change ->
            val sweep = FULL_TURN * change.amount.rupiah / statistics.total.rupiah.toFloat()
            val trim = capDegrees + ARC_GAP_DEGREES / 2f
            drawArc(
                color = colors[index],
                startAngle = start + trim,
                sweepAngle = (sweep - 2f * trim).coerceAtLeast(MIN_SWEEP),
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )
            start += sweep
        }
    }
}
