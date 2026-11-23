package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08, FR-071: rounded usage bar; the fraction is clamped to 0..1 and the colour carries the budget band
@Composable
fun ProgressBar(
    fraction: Float,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(FintrackTheme.spacing.small)
                .clip(FintrackTheme.shapes.full)
                .background(FintrackTheme.colors.surfaces.surfaceVariant),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(color),
        )
    }
}
