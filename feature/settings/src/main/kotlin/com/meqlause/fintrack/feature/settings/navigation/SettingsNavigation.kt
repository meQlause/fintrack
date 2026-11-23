package com.meqlause.fintrack.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.settings.SettingsRoute

// DOC-09, FR-100, FR-120, FR-121: registers Pengaturan in the host graph; the app wires its outgoing rows
public object SettingsNavigation {
    public fun register(
        builder: NavGraphBuilder,
        onBack: () -> Unit,
        onOpenCategories: () -> Unit,
        onOpenBudgets: () -> Unit,
        onOpenMonitoredApps: () -> Unit,
        onOpenAbout: () -> Unit,
        onOpenBatteryHelp: () -> Unit,
        onOpenPinSetup: () -> Unit,
        onOpenPinChange: () -> Unit,
        onOpenPinRemove: () -> Unit,
    ) {
        builder.composable<SettingsDestination> {
            SettingsRoute(
                onBack = onBack,
                onOpenCategories = onOpenCategories,
                onOpenBudgets = onOpenBudgets,
                onOpenMonitoredApps = onOpenMonitoredApps,
                onOpenAbout = onOpenAbout,
                onOpenBatteryHelp = onOpenBatteryHelp,
                onOpenPinSetup = onOpenPinSetup,
                onOpenPinChange = onOpenPinChange,
                onOpenPinRemove = onOpenPinRemove,
            )
        }
    }
}
