package com.meqlause.fintrack.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

/** S-01: the dot indicator; the current page is a wider pill in the brand colour. */
@Composable
internal fun PageDots(
    current: Int,
    count: Int,
    modifier: Modifier = Modifier,
) {
    val spacing = FintrackTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = spacing.large),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val isCurrent = index == current
            val color =
                if (isCurrent) {
                    FintrackTheme.colors.brands.primary
                } else {
                    FintrackTheme.colors.surfaces.surfaceVariant
                }
            val width = if (isCurrent) spacing.large else spacing.small
            Box(
                modifier =
                    Modifier
                        .padding(horizontal = spacing.xxSmall)
                        .size(width = width, height = spacing.small)
                        .clip(FintrackTheme.shapes.full)
                        .background(color),
            )
        }
    }
}
