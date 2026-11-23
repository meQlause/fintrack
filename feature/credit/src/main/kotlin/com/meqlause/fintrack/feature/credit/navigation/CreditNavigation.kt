package com.meqlause.fintrack.feature.credit.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.credit.detail.CreditDetailRoute

// FR-131, FR-132, S-14: registers the credit wallet detail; the host opens it for card and paylater wallets only
public fun NavGraphBuilder.creditDetailDestination(onBack: () -> Unit) {
    composable<CreditDetailDestination> { CreditDetailRoute(onBack = onBack) }
}
