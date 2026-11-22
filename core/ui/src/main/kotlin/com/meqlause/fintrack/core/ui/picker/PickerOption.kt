package com.meqlause.fintrack.core.ui.picker

// S-18: one line of the options drawer; key is what the caller maps back to its own value
data class PickerOption(
    val key: String,
    val label: String,
    val hint: String? = null,
)
