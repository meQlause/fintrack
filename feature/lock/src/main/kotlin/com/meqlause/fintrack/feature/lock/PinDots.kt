package com.meqlause.fintrack.feature.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.security.PinPolicy

// S-02: one dot per PIN digit; the filled dots are the digits typed so far
@Composable
internal fun PinDots(
    filled: Int,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium)) {
        repeat(PinPolicy.LENGTH) { index ->
            val color = if (index < filled) colors.brands.primary else colors.surfaces.outline
            Box(
                modifier =
                    Modifier
                        .size(FintrackTheme.sizes.iconSmall)
                        .clip(FintrackTheme.shapes.full)
                        .background(color),
            )
        }
    }
}
