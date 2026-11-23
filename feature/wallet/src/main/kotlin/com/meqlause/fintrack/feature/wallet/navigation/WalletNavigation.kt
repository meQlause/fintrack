package com.meqlause.fintrack.feature.wallet.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.feature.wallet.WalletRoute

// DOC-09, FR-010: registers the Dompet tab destination in the host graph; a card opens S-19 through onOpenDetail
public fun NavGraphBuilder.walletDestination(onOpenDetail: (WalletId) -> Unit) {
    composable<WalletDestination> { WalletRoute(onOpenDetail = onOpenDetail) }
}
