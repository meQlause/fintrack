package com.meqlause.fintrack.feature.settings

import com.meqlause.fintrack.core.model.ThemeMode

// FR-100, FR-120, FR-121: every action of S-12; defaults do nothing so previews can omit them
internal data class SettingsCallbacks(
    val onBack: () -> Unit = {},
    val onOpenSheet: (SettingsSheet) -> Unit = {},
    val onOpenCategories: () -> Unit = {},
    val onOpenBudgets: () -> Unit = {},
    val onOpenMonitoredApps: () -> Unit = {},
    val onOpenPinSetup: () -> Unit = {},
    val onOpenPinChange: () -> Unit = {},
    val onOpenPinRemove: () -> Unit = {},
    val onPickThemeMode: (ThemeMode) -> Unit = {},
    val onPickPeriodStartDay: (Int) -> Unit = {},
    val onDismissSheet: () -> Unit = {},
    val onBackupCreate: () -> Unit = {},
    val onBackupRestore: () -> Unit = {},
    val onToggleBiometric: (Boolean) -> Unit = {},
    val onToggleHideFromRecents: (Boolean) -> Unit = {},
)
