package com.meqlause.fintrack.feature.credit.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.credit.detail.CreditDetailRoute
import com.meqlause.fintrack.feature.credit.instalment.InstalmentDetailRoute
import com.meqlause.fintrack.feature.credit.instalment.InstalmentListRoute

// FR-131, FR-132, FR-142, S-14, S-16: registers the credit detail and instalment screens; the host opens them
public object CreditNavigation {
    public fun register(
        builder: NavGraphBuilder,
        onBack: () -> Unit,
        onOpenInstalment: (Long) -> Unit,
        onOpenInstalments: () -> Unit,
    ) {
        builder.composable<CreditDetailDestination> {
            CreditDetailRoute(
                onBack = onBack,
                onOpenInstalment = onOpenInstalment,
                onOpenInstalments = onOpenInstalments,
            )
        }
        builder.composable<InstalmentListDestination> {
            InstalmentListRoute(onBack = onBack, onOpenInstalment = onOpenInstalment)
        }
        builder.composable<InstalmentDetailDestination> {
            InstalmentDetailRoute(onBack = onBack)
        }
    }
}
