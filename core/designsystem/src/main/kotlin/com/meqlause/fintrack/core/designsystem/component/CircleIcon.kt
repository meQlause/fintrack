package com.meqlause.fintrack.core.designsystem.component

// DOC-08: the glyph of a round button, its description and whether it is the filled primary one
data class CircleIcon(
    val iconRes: Int,
    val description: String,
    val filled: Boolean = false,
)
