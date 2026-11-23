package com.meqlause.fintrack.feature.wallet.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.feature.wallet.detail.WalletDetailRoute

// FR-017, S-19: registers the wallet detail destination; Pakai and Transfer are handed to the host
public fun NavGraphBuilder.walletDetailDestination(
    onBack: () -> Unit,
    onUse: (WalletId) -> Unit,
    onTransfer: (WalletId) -> Unit,
) {
    composable<WalletDetailDestination> {
        WalletDetailRoute(onBack = onBack, onUse = onUse, onTransfer = onTransfer)
    }
}
