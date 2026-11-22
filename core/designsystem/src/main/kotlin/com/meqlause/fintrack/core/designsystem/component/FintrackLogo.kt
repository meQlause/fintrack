package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

private const val GRID = 32f
private const val CORNER = 10f
private const val STROKE = 2.6f
private const val STEM_X = 11.5f
private const val TOP_Y = 9.5f
private const val BOTTOM_Y = 22.5f
private const val BAR_END_X = 20.5f
private const val MIDDLE_Y = 15.8f
private const val MIDDLE_END_X = 17.5f
private const val COIN_X = 21f
private const val COIN_Y = 21.5f
private const val COIN_RADIUS = 2.3f

// DOC-09: the Fintrack mark drawn on a 32 grid from design/assets/fintrack-logo.svg, scaled to its box
@Composable
fun FintrackLogo(modifier: Modifier = Modifier) {
    val brands = FintrackTheme.colors.brands
    Canvas(modifier = modifier.size(FintrackTheme.sizes.iconTile)) {
        drawLogo(primary = brands.primary, onPrimary = brands.onPrimary)
    }
}

private fun DrawScope.drawLogo(
    primary: Color,
    onPrimary: Color,
) {
    val unit = size.width / GRID
    drawRoundRect(color = primary, cornerRadius = CornerRadius(CORNER * unit, CORNER * unit))
    val stroke = STROKE * unit
    drawLine(
        onPrimary,
        Offset(STEM_X * unit, TOP_Y * unit),
        Offset(BAR_END_X * unit, TOP_Y * unit),
        stroke,
        StrokeCap.Round,
    )
    drawLine(
        onPrimary,
        Offset(STEM_X * unit, TOP_Y * unit),
        Offset(STEM_X * unit, BOTTOM_Y * unit),
        stroke,
        StrokeCap.Round,
    )
    drawLine(
        onPrimary,
        Offset(STEM_X * unit, MIDDLE_Y * unit),
        Offset(MIDDLE_END_X * unit, MIDDLE_Y * unit),
        stroke,
        StrokeCap.Round,
    )
    drawCircle(color = onPrimary, radius = COIN_RADIUS * unit, center = Offset(COIN_X * unit, COIN_Y * unit))
}
