package com.meqlause.fintrack.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.settings.MonitoredAppsRoute

// DOC-09, FR-081, FR-082, FR-090: registers Aplikasi dipantau (S-13) in the host graph
public object MonitoredAppsNavigation {
    public fun register(
        builder: NavGraphBuilder,
        onBack: () -> Unit,
    ) {
        builder.composable<MonitoredAppsDestination> {
            MonitoredAppsRoute(onBack = onBack)
        }
    }
}
