package com.meqlause.fintrack.feature.lock

import com.meqlause.fintrack.feature.lock.navigation.PinSetupMode

// FR-100, DOC-10: which PIN setup step is shown and whether the PIN is set, changed or removed
internal data class PinSetupPrompt(
    val mode: PinSetupMode = PinSetupMode.SET,
    val step: PinStep = PinStep.NEW,
)
