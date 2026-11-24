package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Dp
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

private val ART_WIDTH: Dp = Dp(value = 132f)
private val ART_HEIGHT: Dp = Dp(value = 110f)
private const val VIEW_WIDTH = 120f
private const val DISC_X = 60f
private const val DISC_Y = 54f
private const val DISC_RADIUS = 40f
private const val COIN_X = 83f
private const val COIN_Y = 68f
private const val COIN_RADIUS = 12f
private const val HAIRLINE = 1.5f
private const val GLYPH_STROKE = 1.8f
private const val TEXT_LINE_STROKE = 3f
private const val RECEIPT = "M42 20h36v54l-6-4-6 4-6-4-6 4-6-4-6 4z"
private const val TEXT_LINES = "M50 32h20M50 40h13M50 48h17M50 56h9"
private const val COIN_GLYPH = "M79 72v-8h3a2.5 2.5 0 0 1 0 5h-3M82 69l3 3"
private const val SPARKLE_BIG = "M28 24l1.6 4.4L34 30l-4.4 1.6L28 36l-1.6-4.4L22 30l4.4-1.6z"
private const val SPARKLE_SMALL = "M95 20l1 2.8L99 24l-3 1.2L95 28l-1-2.8L91 24l3-1.2z"

// DOC-08: the mockup empty-state art, a receipt with an R coin and two sparkles on a soft disc
@Composable
fun EmptyIllustration(modifier: Modifier = Modifier) {
    val colors = FintrackTheme.colors
    val palette = listOf(colors.surfaces.surfaceVariant, colors.surfaces.surface, colors.surfaces.outline)
    val accent = listOf(colors.brands.primaryContainer, colors.brands.primary)
    Canvas(modifier = modifier.size(width = ART_WIDTH, height = ART_HEIGHT)) {
        scale(scale = size.width / VIEW_WIDTH, pivot = Offset.Zero) {
            drawDisc(palette)
            drawCoin(accent)
        }
    }
}

private fun DrawScope.drawDisc(palette: List<Color>) {
    drawCircle(color = palette[0], radius = DISC_RADIUS, center = Offset(DISC_X, DISC_Y))
    val receipt = pathOf(RECEIPT)
    drawPath(receipt, palette[1], style = Fill)
    drawPath(receipt, palette[2], style = Stroke(HAIRLINE, join = StrokeJoin.Round))
    drawPath(pathOf(TEXT_LINES), palette[2], style = lineStroke(TEXT_LINE_STROKE))
}

private fun DrawScope.drawCoin(accent: List<Color>) {
    drawCircle(accent[0], COIN_RADIUS, Offset(COIN_X, COIN_Y))
    drawCircle(accent[1], COIN_RADIUS, Offset(COIN_X, COIN_Y), style = Stroke(HAIRLINE))
    drawPath(pathOf(COIN_GLYPH), accent[1], style = lineStroke(GLYPH_STROKE))
    listOf(SPARKLE_BIG, SPARKLE_SMALL).forEach { drawPath(pathOf(it), accent[1], style = Fill) }
}

private fun pathOf(data: String): Path = PathParser().parsePathString(data).toPath()

private fun lineStroke(width: Float): Stroke = Stroke(width, cap = StrokeCap.Round, join = StrokeJoin.Round)
