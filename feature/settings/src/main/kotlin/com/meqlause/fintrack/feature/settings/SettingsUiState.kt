package com.meqlause.fintrack.feature.settings

import com.meqlause.fintrack.core.model.ThemeMode

private const val DEFAULT_PERIOD_START_DAY = 1

// FR-100, FR-101, FR-104, FR-120, FR-121: what S-12 renders; sheet is the open picker drawer, or null
internal data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val periodStartDay: Int = DEFAULT_PERIOD_START_DAY,
    val pinEnabled: Boolean = false,
    val biometricSupported: Boolean = false,
    val biometricEnabled: Boolean = false,
    val hideFromRecents: Boolean = false,
    val sheet: SettingsSheet? = null,
)
