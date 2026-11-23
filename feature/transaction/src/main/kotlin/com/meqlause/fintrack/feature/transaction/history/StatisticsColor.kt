package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.ui.graphics.Color
import com.meqlause.fintrack.core.designsystem.theme.FintrackColors

// S-08: category slices and bars take their colours from the design tokens, in a fixed order
internal fun statisticsPalette(colors: FintrackColors): List<Color> =
    listOf(
        colors.brands.primary,
        colors.semantics.warning,
        colors.semantics.transfer,
        colors.semantics.income,
        colors.semantics.expense,
        colors.texts.faint,
    )
