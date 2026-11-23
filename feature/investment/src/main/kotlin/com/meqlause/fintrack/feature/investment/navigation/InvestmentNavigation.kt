package com.meqlause.fintrack.feature.investment.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.feature.investment.InvestmentRoute

// DOC-09, FR-173: registers the Investasi tab in the host graph; an account opens S-20 through onOpenAccount
public object InvestmentNavigation {
    public fun register(
        builder: NavGraphBuilder,
        onOpenAccount: (WalletId) -> Unit,
    ) {
        builder.composable<InvestmentDestination> { InvestmentRoute(onOpenAccount = onOpenAccount) }
    }
}
