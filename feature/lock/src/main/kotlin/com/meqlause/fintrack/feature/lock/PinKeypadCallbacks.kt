package com.meqlause.fintrack.feature.lock

// FR-100: the digit and backspace actions shared by the lock screen and the PIN setup; defaults do nothing
internal data class PinKeypadCallbacks(
    val onDigit: (String) -> Unit = {},
    val onBackspace: () -> Unit = {},
)
