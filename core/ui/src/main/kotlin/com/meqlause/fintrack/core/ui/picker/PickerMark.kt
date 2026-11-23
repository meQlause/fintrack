package com.meqlause.fintrack.core.ui.picker

// S-18: whether a picker row is the current choice, or is blocked because another side already holds it
data class PickerMark(
    val isSelected: Boolean = false,
    val isBlocked: Boolean = false,
)
