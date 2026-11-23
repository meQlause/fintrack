package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.ui.graphics.Color

// DOC-08: one cell of a stat grid; a null color shows the value in the normal text colour
data class ScreenStat(
    val label: String,
    val value: String,
    val color: Color? = null,
)
