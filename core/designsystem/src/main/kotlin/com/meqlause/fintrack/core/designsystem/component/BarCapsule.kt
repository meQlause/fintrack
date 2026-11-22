package com.meqlause.fintrack.core.designsystem.component

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.meqlause.fintrack.core.designsystem.theme.FintrackSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import android.graphics.Path as AndroidPath

// DOC-09: the pill of the bottom bar; a notch is cut out of its topEdge edge under the selected tab's bubble
@Composable
internal fun BarCapsule(
    centreX: () -> Float,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors.surfaces
    val sizes = FintrackTheme.sizes
    Canvas(modifier = modifier.fillMaxSize()) {
        val path = barCapsulePath(size.width, size.height, sizes, centreX())
        drawPath(path = path, color = colors.surface)
        drawPath(path = path, color = colors.outline, style = Stroke(width = sizes.hairline.toPx()))
    }
}

// DOC-09: capsule minus a circle centred on its topEdge edge, so the notch follows the bubble
private fun DrawScope.barCapsulePath(
    width: Float,
    height: Float,
    sizes: FintrackSizes,
    notchX: Float,
): Path {
    val topEdge = sizes.navBarCapsuleTop.toPx()
    val radius = (height - topEdge) / 2f
    val capsule =
        AndroidPath().apply {
            addRoundRect(RectF(0f, topEdge, width, height), radius, radius, AndroidPath.Direction.CW)
        }
    val notch =
        AndroidPath().apply {
            addCircle(notchX, topEdge, sizes.navBarNotchRadius.toPx(), AndroidPath.Direction.CW)
        }
    val result = AndroidPath().apply { op(capsule, notch, AndroidPath.Op.DIFFERENCE) }
    return result.asComposePath()
}
