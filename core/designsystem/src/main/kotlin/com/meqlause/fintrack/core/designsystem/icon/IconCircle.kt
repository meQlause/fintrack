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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.component.ComponentCanvas
import com.meqlause.fintrack.core.designsystem.theme.CategoryPalette
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08, DOC-09: category icon in a 38 px rounded box tinted with its colour, as the prototype .row; decorative
@Composable
fun IconCircle(
    look: IconLook,
    modifier: Modifier = Modifier,
    tile: IconBox? = null,
) {
    val box = tile ?: categoryTile()
    val surface = FintrackTheme.colors.surfaces.background
    val tint = CategoryPalette.colorFor(colorKey = look.colorKey, surface = surface)
    val alpha =
        if (CategoryPalette.isDarkSurface(surface)) {
            FintrackTheme.alphas.categoryTintDark
        } else {
            FintrackTheme.alphas.categoryTintLight
        }
    Box(
        modifier =
            modifier
                .size(box.size)
                .clip(box.shape)
                .background(tint.copy(alpha = alpha)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(FintrackIcons.drawableFor(look.iconKey)),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(box.glyph),
        )
    }
}

// DOC-08: the 38 px category tile of the list rows
@Composable
private fun categoryTile(): IconBox =
    IconBox(
        size = FintrackTheme.sizes.categoryIcon,
        glyph = FintrackTheme.sizes.categoryGlyph,
        shape = FintrackTheme.shapes.categoryIcon,
    )

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
        IconCircle(look = IconLook(iconKey = "food", colorKey = "orange"))
        IconCircle(look = IconLook(iconKey = "transport", colorKey = "blue"))
        IconCircle(look = IconLook(iconKey = "salary", colorKey = "green"))
    }
}
