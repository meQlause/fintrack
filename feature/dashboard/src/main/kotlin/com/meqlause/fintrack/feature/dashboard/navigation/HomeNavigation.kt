package com.meqlause.fintrack.feature.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.feature.dashboard.HomeRoute

// DOC-09, FR-040..043: registers Beranda in the host graph; every step out of S-03 is handed to the host
public fun NavGraphBuilder.homeDestination(
    onOpenSettings: () -> Unit,
    onOpenInbox: () -> Unit,
    onOpenWallet: (WalletId) -> Unit,
    onOpenWallets: () -> Unit,
    onOpenHistory: () -> Unit,
    onQuickEntry: () -> Unit,
    onApplyShortcut: (QuickEntryId) -> Unit,
    onOpenTransaction: (TransactionId) -> Unit,
) {
    composable<HomeDestination> {
        HomeRoute(
            onOpenSettings = onOpenSettings,
            onOpenInbox = onOpenInbox,
            onOpenWallet = onOpenWallet,
            onOpenWallets = onOpenWallets,
            onOpenHistory = onOpenHistory,
            onQuickEntry = onQuickEntry,
            onApplyShortcut = onApplyShortcut,
            onOpenTransaction = onOpenTransaction,
        )
    }
}
