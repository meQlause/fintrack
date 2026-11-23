package com.meqlause.fintrack.feature.wallet

import com.meqlause.fintrack.core.domain.networth.NetWorth

// FR-010..016, S-09: every state of the Dompet tab; the editor and an open picker can sit on top of a list state
internal sealed interface WalletUiState {
    // S-09: wallets are still being read from the repository
    data object Loading : WalletUiState

    // S-09: no wallet yet; the editor opens from the add button
    data class Empty(
        val editor: WalletEditor?,
        val picker: WalletPicker?,
    ) : WalletUiState

    // S-09: Kekayaan bersih (BR-053) above the groups; archived wallets are collapsed (FR-012)
    data class Success(
        val netWorth: NetWorth,
        val sections: List<WalletSection>,
        val archived: List<WalletItem>,
        val archivedExpanded: Boolean,
        val editor: WalletEditor?,
        val picker: WalletPicker?,
    ) : WalletUiState
}
