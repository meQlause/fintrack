package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: prototype 36 px round action; filled primary with a check, or outlined and muted for dismiss
@Composable
fun ScreenCircleButton(
    iconRes: Int,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
    enabled: Boolean = true,
) {
    val colors = FintrackTheme.colors
    val base =
        if (filled) {
            Modifier.background(colors.brands.primary)
        } else {
            Modifier.border(FintrackTheme.sizes.hairline, colors.surfaces.outline, CircleShape)
        }
    Box(
        modifier =
            modifier
                .size(FintrackScreenSizes.circleButton)
                .alpha(if (enabled) 1f else FintrackScreenSizes.disabledAlpha)
                .clip(CircleShape)
                .then(base)
                .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = description,
            tint = if (filled) colors.brands.onPrimary else colors.texts.onSurfaceVariant,
            modifier = Modifier.size(FintrackScreenSizes.circleGlyph),
        )
    }
}
