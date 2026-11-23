package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.ui.graphics.Color

// DOC-08: the label, glyph and glyph colour of an action button
data class ScreenAction(
    val text: String,
    val iconRes: Int,
    val tint: Color,
)
