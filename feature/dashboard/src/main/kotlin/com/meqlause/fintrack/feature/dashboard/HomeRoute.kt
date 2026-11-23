package com.meqlause.fintrack.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.WalletId

// DOC-03, FR-040..043: stateful entry of S-03; the only composable that touches HomeViewModel
@Composable
internal fun HomeRoute(
    onOpenSettings: () -> Unit,
    onOpenInbox: () -> Unit,
    onOpenWallet: (WalletId) -> Unit,
    onOpenCreditWallet: (WalletId) -> Unit,
    onOpenInstalments: () -> Unit,
    onOpenLoans: () -> Unit,
    onOpenWallets: () -> Unit,
    onOpenHistory: () -> Unit,
    onQuickEntry: () -> Unit,
    onApplyShortcut: (QuickEntryId) -> Unit,
    onOpenTransaction: (TransactionId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        callbacks =
            HomeCallbacks(
                onOpenSettings = onOpenSettings,
                onOpenInbox = onOpenInbox,
                onToggleTheme = viewModel::toggleTheme,
                onToggleHidden = viewModel::toggleHidden,
                onOpenSummary = viewModel::openSummary,
                onOpenCategories = viewModel::openCategories,
                onCloseDrawer = viewModel::closeDrawer,
                onOpenWallet = onOpenWallet,
                onOpenCreditWallet = onOpenCreditWallet,
                onOpenInstalments = onOpenInstalments,
                onOpenLoans = onOpenLoans,
                onOpenWallets = onOpenWallets,
                onQuickEntry = onQuickEntry,
                onOpenTransaction = onOpenTransaction,
                onOpenHistory = onOpenHistory,
                onApplyShortcut = onApplyShortcut,
                onOpenShortcuts = viewModel::openShortcuts,
                onDeleteShortcut = viewModel::deleteShortcut,
                onRestoreShortcut = viewModel::restoreShortcut,
            ),
        modifier = modifier,
    )
}
