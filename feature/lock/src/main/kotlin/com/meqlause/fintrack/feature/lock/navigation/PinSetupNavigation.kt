package com.meqlause.fintrack.feature.lock.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.lock.PinSetupRoute

// FR-100, S-01, S-12: registers the PIN setup screen; onDone runs after the PIN is stored or removed
public object PinSetupNavigation {
    public fun register(
        builder: NavGraphBuilder,
        onDone: () -> Unit,
        onCancel: () -> Unit,
    ) {
        builder.composable<PinSetupDestination> {
            PinSetupRoute(onDone = onDone, onCancel = onCancel)
        }
    }
}
