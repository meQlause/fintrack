package com.meqlause.fintrack.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.settings.BatteryHelpRoute

// DOC-07, FR-090: registers the battery-optimisation help page in the host graph
public object BatteryHelpNavigation {
    public fun register(
        builder: NavGraphBuilder,
        onBack: () -> Unit,
    ) {
        builder.composable<BatteryHelpDestination> {
            BatteryHelpRoute(onBack = onBack)
        }
    }
}
