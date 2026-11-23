package com.meqlause.fintrack.feature.settings

import com.meqlause.fintrack.core.model.ThemeMode

private const val DEFAULT_PERIOD_START_DAY = 1

// FR-100, FR-101, FR-104, FR-120, FR-121: what S-12 renders; sheet is the open picker drawer, or null
internal data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val periodStartDay: Int = DEFAULT_PERIOD_START_DAY,
    val pinEnabled: Boolean = false,
    val biometric: BiometricState = BiometricState(),
    val hideFromRecents: Boolean = false,
    val sheet: SettingsSheet? = null,
)

// FR-104: whether the device can use biometrics for the PIN, and whether the user turned that on
internal data class BiometricState(
    val supported: Boolean = false,
    val enabled: Boolean = false,
)
