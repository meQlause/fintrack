package com.meqlause.fintrack.feature.wallet.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.feature.wallet.WalletRoute

// DOC-09, FR-010: registers the Dompet tab in the host graph; a card opens S-19 (S-14 for credit) via onOpenDetail
public object WalletNavigation {
    public fun register(
        builder: NavGraphBuilder,
        onOpenDetail: (WalletId, WalletType) -> Unit,
        onOpenLoans: () -> Unit,
    ) {
        builder.composable<WalletDestination> {
            WalletRoute(onOpenDetail = onOpenDetail, onOpenLoans = onOpenLoans)
        }
    }
}
