package com.meqlause.fintrack.feature.onboarding

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * S-01, FR-001..FR-004: registers onboarding in the app NavHost. [onFinished] leaves the flow for the Dashboard;
 * [onSetupPin] opens the PIN setup from the security page.
 */
public object OnboardingGraph {
    public fun register(
        builder: NavGraphBuilder,
        onFinished: () -> Unit,
        onSetupPin: () -> Unit,
    ) {
        builder.composable<OnboardingDestination> {
            OnboardingRoute(onFinished = onFinished, onSetupPin = onSetupPin)
        }
    }
}
