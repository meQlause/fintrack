package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// DOC-08: colour carries meaning: money direction, budget warning and error
@Immutable
data class SemanticColors(
    val income: Color,
    val expense: Color,
    val transfer: Color,
    val warning: Color,
    val error: Color,
)
