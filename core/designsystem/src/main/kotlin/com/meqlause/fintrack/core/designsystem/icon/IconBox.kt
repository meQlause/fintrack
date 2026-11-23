package com.meqlause.fintrack.core.designsystem.icon

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

// DOC-08: the outer size, glyph size and shape of an icon tile
data class IconBox(
    val size: Dp,
    val glyph: Dp,
    val shape: Shape,
)
