package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: prototype dashed add button, 50 px tall with a plus and a muted label
@Composable
fun ScreenDashedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val outline = FintrackTheme.colors.surfaces.outline
    val corner = FintrackScreenSizes.dashedCorner
    val cornerPx = with(LocalDensity.current) { FintrackScreenSizes.dashedRadius.toPx() }
    val strokePx = with(LocalDensity.current) { FintrackTheme.sizes.hairline.toPx() }
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(FintrackScreenSizes.dashedHeight)
                .clip(corner)
                .clickable(onClick = onClick)
                .drawBehind { dashedBorder(outline, corner = cornerPx, stroke = strokePx) },
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = null,
            tint = FintrackTheme.colors.texts.onSurfaceVariant,
            modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
        )
        Text(
            text = text,
            style = FintrackScreenText.button(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}

private fun DrawScope.dashedBorder(
    color: Color,
    corner: Float,
    stroke: Float,
) {
    val pattern = PathEffect.dashPathEffect(floatArrayOf(stroke * 3f, stroke * 3f))
    drawRoundRect(
        color = color,
        cornerRadius = CornerRadius(corner),
        style = Stroke(width = stroke, pathEffect = pattern),
    )
}
