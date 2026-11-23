package com.meqlause.fintrack.feature.investment.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.investment.detail.InvestmentDetailRoute

// FR-173, S-20: registers the investment detail; "Tambah dana" and "Tarik" both leave through onTransfer
public fun NavGraphBuilder.investmentDetailDestination(
    onBack: () -> Unit,
    onTransfer: () -> Unit,
) {
    composable<InvestmentDetailDestination> {
        InvestmentDetailRoute(onBack = onBack, onTransfer = onTransfer)
    }
}
