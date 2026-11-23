package com.meqlause.fintrack.feature.lock

// FR-100: the typed digits, the step and the first entry kept while the PIN is confirmed
internal data class PinSetupEntry(
    val step: PinStep,
    val digits: String = "",
    val firstDigits: String = "",
    val notice: PinNotice = PinNotice.NONE,
    val shakeCount: Int = 0,
    val done: Boolean = false,
)
