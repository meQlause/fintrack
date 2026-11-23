package com.meqlause.fintrack.feature.credit.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.credit.detail.CreditDetailRoute
import com.meqlause.fintrack.feature.credit.instalment.InstalmentDetailRoute
import com.meqlause.fintrack.feature.credit.instalment.InstalmentListRoute

// FR-131, FR-132, FR-142, S-14, S-16: registers the credit wallet detail and the instalment screens; the host opens them
public fun NavGraphBuilder.creditDetailDestination(
    onBack: () -> Unit,
    onOpenInstalment: (Long) -> Unit,
    onOpenInstalments: () -> Unit,
) {
    composable<CreditDetailDestination> {
        CreditDetailRoute(onBack = onBack, onOpenInstalment = onOpenInstalment, onOpenInstalments = onOpenInstalments)
    }
    composable<InstalmentListDestination> {
        InstalmentListRoute(onBack = onBack, onOpenInstalment = onOpenInstalment)
    }
    composable<InstalmentDetailDestination> {
        InstalmentDetailRoute(onBack = onBack)
    }
}
