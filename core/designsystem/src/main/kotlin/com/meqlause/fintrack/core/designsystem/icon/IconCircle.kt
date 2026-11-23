package com.meqlause.fintrack.core.designsystem.icon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.meqlause.fintrack.core.designsystem.component.ComponentCanvas
import com.meqlause.fintrack.core.designsystem.theme.CategoryPalette
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08, DOC-09: category icon in a 38 px rounded box tinted with its colour, as the prototype .row; decorative
@Composable
fun IconCircle(
    iconKey: String,
    colorKey: String,
    modifier: Modifier = Modifier,
    size: Dp = FintrackTheme.sizes.categoryIcon,
    glyph: Dp = FintrackTheme.sizes.categoryGlyph,
    shape: Shape = FintrackTheme.shapes.categoryIcon,
) {
    val surface = FintrackTheme.colors.surfaces.background
    val tint = CategoryPalette.colorFor(colorKey = colorKey, surface = surface)
    val alpha =
        if (CategoryPalette.isDarkSurface(surface)) {
            FintrackTheme.alphas.categoryTintDark
        } else {
            FintrackTheme.alphas.categoryTintLight
        }
    Box(
        modifier =
            modifier
                .size(size)
                .clip(shape)
                .background(tint.copy(alpha = alpha)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(FintrackIcons.drawableFor(iconKey)),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(glyph),
        )
    }
}

@Preview(name = "IconCircle light", showBackground = true)
@Composable
internal fun IconCircleLightPreview() {
    ComponentCanvas(darkTheme = false) { IconCirclePreviewRow() }
}

@Preview(name = "IconCircle dark", showBackground = true)
@Composable
internal fun IconCircleDarkPreview() {
    ComponentCanvas(darkTheme = true) { IconCirclePreviewRow() }
}

@Composable
private fun IconCirclePreviewRow() {
    Row {
        IconCircle(iconKey = "food", colorKey = "orange")
        IconCircle(iconKey = "transport", colorKey = "blue")
        IconCircle(iconKey = "salary", colorKey = "green")
    }
}
