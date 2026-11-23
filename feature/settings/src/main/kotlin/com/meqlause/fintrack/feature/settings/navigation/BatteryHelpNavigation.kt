package com.meqlause.fintrack.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.settings.BatteryHelpRoute

// DOC-07, FR-090: registers the battery-optimisation help page in the host graph
public fun NavGraphBuilder.batteryHelpDestination(onBack: () -> Unit) {
    composable<BatteryHelpDestination> {
        BatteryHelpRoute(onBack = onBack)
    }
}
