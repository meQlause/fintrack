package com.meqlause.fintrack.core.designsystem.icon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.meqlause.fintrack.core.designsystem.theme.CategoryPalette
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: a rounded square of any size tinted with a palette colour, as the prototype hero icons (52 px, 16 px radius)
@Composable
fun TintedIconBox(
    iconKey: String,
    colorKey: String,
    size: Dp,
    glyph: Dp,
    corner: Dp,
    modifier: Modifier = Modifier,
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
                .clip(RoundedCornerShape(corner))
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
