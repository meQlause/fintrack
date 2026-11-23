package com.meqlause.fintrack.feature.investment.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.investment.detail.InvestmentDetailRoute

// FR-173, S-20: registers the investment detail; "Tambah dana" and "Tarik" both leave through onTransfer
public object InvestmentDetailNavigation {
    public fun register(
        builder: NavGraphBuilder,
        onBack: () -> Unit,
        onTransfer: () -> Unit,
    ) {
        builder.composable<InvestmentDetailDestination> {
            InvestmentDetailRoute(onBack = onBack, onTransfer = onTransfer)
        }
    }
}
