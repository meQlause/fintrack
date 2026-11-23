package com.meqlause.fintrack.feature.lock.navigation

import kotlinx.serialization.Serializable

// FR-100, S-12, S-01: the PIN setup screen; mode says whether a PIN is set, changed or removed
@Serializable
public data class PinSetupDestination(
    public val mode: PinSetupMode = PinSetupMode.SET,
)
