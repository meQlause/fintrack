package com.meqlause.fintrack.core.ui.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: the round mark at the end of an option row; the chosen one is ringed and dotted in the primary colour
@Composable
internal fun OptionRadio(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    val edge = if (isSelected) colors.brands.primary else colors.surfaces.outline
    Box(
        modifier =
            modifier
                .size(FintrackScreenSizes.radioOuter)
                .clip(FintrackTheme.shapes.full)
                .border(FintrackTheme.sizes.hairline * 2, edge, FintrackTheme.shapes.full),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Box(
                modifier =
                    Modifier
                        .size(FintrackScreenSizes.radioDot)
                        .clip(FintrackTheme.shapes.full)
                        .background(colors.brands.primary),
            )
        }
    }
}
