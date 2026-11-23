package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: prototype status pill, 11 px bold text on a tinted background, 7 px radius
@Composable
fun ScreenChip(
    text: String,
    background: Color,
    foreground: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = FintrackScreenText.style(size = 11f, weight = 700),
        color = foreground,
        modifier =
            modifier
                .clip(RoundedCornerShape(FintrackScreenSizes.badgeCorner))
                .background(background)
                .padding(horizontal = FintrackTheme.spacing.small, vertical = FintrackScreenSizes.badgePadding),
    )
}
