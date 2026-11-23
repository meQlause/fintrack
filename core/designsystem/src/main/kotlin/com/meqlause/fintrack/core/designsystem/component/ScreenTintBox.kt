package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: the neutral 38 px square (surface2 background, 12 px radius, 18 px glyph) that leads cycle and detail rows
@Composable
fun ScreenTintBox(
    iconRes: Int,
    modifier: Modifier = Modifier,
    tint: Color = FintrackTheme.colors.texts.onSurfaceVariant,
) {
    Box(
        modifier =
            modifier
                .size(FintrackTheme.sizes.categoryIcon)
                .clip(FintrackTheme.shapes.categoryIcon)
                .background(FintrackTheme.colors.surfaces.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(FintrackTheme.sizes.categoryGlyph),
        )
    }
}
