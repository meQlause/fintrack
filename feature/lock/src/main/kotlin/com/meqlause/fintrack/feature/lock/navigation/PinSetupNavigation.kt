package com.meqlause.fintrack.feature.lock.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.lock.PinSetupRoute

// FR-100, S-01, S-12: registers the PIN setup screen; onDone runs after the PIN is stored or removed
public fun NavGraphBuilder.pinSetupDestination(
    onDone: () -> Unit,
    onCancel: () -> Unit,
) {
    composable<PinSetupDestination> {
        PinSetupRoute(onDone = onDone, onCancel = onCancel)
    }
}
