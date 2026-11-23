package com.meqlause.fintrack.feature.lock

import com.meqlause.fintrack.feature.lock.navigation.PinSetupMode

// FR-100, DOC-10: what the PIN setup screen renders; the recovery warning is shown on the new-PIN step
internal data class PinSetupUiState(
    val mode: PinSetupMode = PinSetupMode.SET,
    val step: PinStep = PinStep.NEW,
    val digits: String = "",
    val notice: PinNotice = PinNotice.NONE,
    val remainingSeconds: Long = 0L,
    val shakeCount: Int = 0,
    val done: Boolean = false,
)
