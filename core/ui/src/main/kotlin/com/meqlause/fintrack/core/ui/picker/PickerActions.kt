package com.meqlause.fintrack.core.ui.picker

// S-18: what a picker drawer does when the user picks a value and when it closes
data class PickerActions<T>(
    val onPick: (T) -> Unit,
    val onClose: () -> Unit,
)
