package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// DOC-08: screen, card and hairline colours
@Immutable
data class SurfaceColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val outline: Color,
)
