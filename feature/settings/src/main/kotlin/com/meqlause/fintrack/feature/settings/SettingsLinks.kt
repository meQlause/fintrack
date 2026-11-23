package com.meqlause.fintrack.feature.settings

/** FR-120, FR-121: the back button and the rows that open another screen. */
public data class SettingsLinks(
    val onBack: () -> Unit = {},
    val onOpenCategories: () -> Unit = {},
    val onOpenBudgets: () -> Unit = {},
    val onOpenMonitoredApps: () -> Unit = {},
    val onOpenAbout: () -> Unit = {},
    val onOpenBatteryHelp: () -> Unit = {},
)
