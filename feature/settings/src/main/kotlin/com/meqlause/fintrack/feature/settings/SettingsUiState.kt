package com.meqlause.fintrack.feature.settings

import com.meqlause.fintrack.core.model.ThemeMode

private const val DEFAULT_PERIOD_START_DAY = 1

// FR-120, FR-121: what S-12 renders; sheet is the picker drawer that is open, or null when none is
internal data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val periodStartDay: Int = DEFAULT_PERIOD_START_DAY,
    val sheet: SettingsSheet? = null,
)
