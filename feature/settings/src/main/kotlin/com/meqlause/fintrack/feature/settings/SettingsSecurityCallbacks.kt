package com.meqlause.fintrack.feature.settings

/** FR-100: the PIN rows and the biometric switch. */
public data class SettingsSecurityCallbacks(
    val onOpenPinSetup: () -> Unit = {},
    val onOpenPinChange: () -> Unit = {},
    val onOpenPinRemove: () -> Unit = {},
    val onToggleBiometric: (Boolean) -> Unit = {},
)
