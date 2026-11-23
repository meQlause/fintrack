package com.meqlause.fintrack.feature.wallet.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meqlause.fintrack.core.model.WalletId

// FR-017, S-19: stateful entry of the wallet detail; the only composable that touches WalletDetailViewModel
@Composable
internal fun WalletDetailRoute(
    onBack: () -> Unit,
    onUse: (WalletId) -> Unit,
    onTransfer: (WalletId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: WalletDetailViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    WalletDetailScreen(
        state = state,
        callbacks =
            WalletDetailCallbacks(
                onBack = onBack,
                onToggleExpanded = viewModel::toggleExpanded,
                onUse = { onUse(viewModel.walletId) },
                onTransfer = { onTransfer(viewModel.walletId) },
            ),
        modifier = modifier,
    )
}
