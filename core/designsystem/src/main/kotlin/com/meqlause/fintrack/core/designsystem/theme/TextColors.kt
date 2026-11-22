package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// DOC-08: primary, secondary and faint text colours
@Immutable
data class TextColors(
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val faint: Color,
)
