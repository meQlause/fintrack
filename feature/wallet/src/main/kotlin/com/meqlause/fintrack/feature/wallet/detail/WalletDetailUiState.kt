package com.meqlause.fintrack.feature.wallet.detail

import com.meqlause.fintrack.feature.wallet.WalletItem

// FR-017, S-19: every state of the wallet detail; Missing when the wallet id no longer exists
internal sealed interface WalletDetailUiState {
    // S-19: the wallet and its transactions are still being read
    data object Loading : WalletDetailUiState

    // S-19: the wallet was removed or never existed
    data object Missing : WalletDetailUiState

    // S-19: balance and the wallet's transactions, newest first; expanded shows the full list (FR-017)
    data class Success(
        val item: WalletItem,
        val rows: List<WalletDetailRow>,
        val expanded: Boolean,
        val stats: WalletDetailStats = WalletDetailStats.EMPTY,
    ) : WalletDetailUiState
}
