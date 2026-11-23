package com.meqlause.fintrack.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.settings.SettingsRoute

// DOC-09, FR-100, FR-120, FR-121: registers Pengaturan in the host graph; the app wires its outgoing rows
public fun NavGraphBuilder.settingsDestination(
    onBack: () -> Unit,
    onOpenCategories: () -> Unit,
    onOpenBudgets: () -> Unit,
    onOpenMonitoredApps: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenPinSetup: () -> Unit,
    onOpenPinChange: () -> Unit,
    onOpenPinRemove: () -> Unit,
) {
    composable<SettingsDestination> {
        SettingsRoute(
            onBack = onBack,
            onOpenCategories = onOpenCategories,
            onOpenBudgets = onOpenBudgets,
            onOpenMonitoredApps = onOpenMonitoredApps,
            onOpenAbout = onOpenAbout,
            onOpenPinSetup = onOpenPinSetup,
            onOpenPinChange = onOpenPinChange,
            onOpenPinRemove = onOpenPinRemove,
        )
    }
}
