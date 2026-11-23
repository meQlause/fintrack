package com.meqlause.fintrack.feature.dashboard

import androidx.compose.ui.graphics.Color
import com.meqlause.fintrack.core.designsystem.theme.FintrackColors

// S-03: colour of the n-th segment of a wallet or category bar; the palette repeats after five segments
internal fun homeSegmentColor(
    colors: FintrackColors,
    index: Int,
): Color {
    val palette =
        listOf(
            colors.brands.primary,
            colors.semantics.expense,
            colors.semantics.income,
            colors.semantics.transfer,
            colors.semantics.warning,
        )
    return palette[index % palette.size]
}
