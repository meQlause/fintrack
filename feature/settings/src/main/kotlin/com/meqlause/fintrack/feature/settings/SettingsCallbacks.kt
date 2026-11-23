package com.meqlause.fintrack.feature.settings

import com.meqlause.fintrack.core.model.ThemeMode

// FR-100, FR-120, FR-121: every action of S-12, grouped by the part of the screen it belongs to; defaults do nothing
internal data class SettingsCallbacks(
    val links: SettingsLinks = SettingsLinks(),
    val security: SettingsSecurityCallbacks = SettingsSecurityCallbacks(),
    val preferences: SettingsPreferenceCallbacks = SettingsPreferenceCallbacks(),
    val backup: SettingsBackupCallbacks = SettingsBackupCallbacks(),
)

// FR-101, FR-104, FR-120: the theme and period choices, the picker drawer and the hide-from-recents switch
internal data class SettingsPreferenceCallbacks(
    val onOpenSheet: (SettingsSheet) -> Unit = {},
    val onPickThemeMode: (ThemeMode) -> Unit = {},
    val onPickPeriodStartDay: (Int) -> Unit = {},
    val onDismissSheet: () -> Unit = {},
    val onToggleHideFromRecents: (Boolean) -> Unit = {},
)

// FR-110, FR-113: the backup and CSV rows
internal data class SettingsBackupCallbacks(
    val onBackupCreate: () -> Unit = {},
    val onBackupRestore: () -> Unit = {},
    val onBackupReminder: () -> Unit = {},
    val onBackupCsv: () -> Unit = {},
)
