package com.meqlause.fintrack.feature.wallet.detail

// FR-017, S-19: every user action of the wallet detail, passed to the stateless screen as plain lambdas
internal data class WalletDetailCallbacks(
    val onBack: () -> Unit = {},
    val onToggleExpanded: () -> Unit = {},
    val onUse: () -> Unit = {},
    val onTransfer: () -> Unit = {},
)
