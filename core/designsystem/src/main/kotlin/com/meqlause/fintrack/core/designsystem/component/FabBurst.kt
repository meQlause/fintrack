package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.meqlause.fintrack.core.designsystem.theme.FintrackSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import kotlin.math.cos
import kotlin.math.sin

private const val DOT_COUNT = 6
private const val ANGLE_STEP_DEGREES = 60.0
private const val BURST_DONE = 1f

// DOC-09: six primary dots fly out of the add button while progress runs from 0 to 1, then vanish
@Composable
internal fun FabBurst(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    if (progress >= BURST_DONE) return
    val primary = FintrackTheme.colors.brands.primary
    val sizes = FintrackTheme.sizes
    Canvas(modifier = modifier) {
        drawDots(primary = primary, progress = progress, sizes = sizes)
    }
}

private fun DrawScope.drawDots(
    primary: Color,
    progress: Float,
    sizes: FintrackSizes,
) {
    val travel = sizes.navBarTravel.toPx() * progress
    val radius = sizes.navBarDot.toPx() / 2f
    repeat(DOT_COUNT) { index ->
        val angle = Math.toRadians(index * ANGLE_STEP_DEGREES)
        val offset = Offset((cos(angle) * travel).toFloat(), (sin(angle) * travel).toFloat())
        drawCircle(color = primary, radius = radius, center = center + offset, alpha = BURST_DONE - progress)
    }
}
