package com.meqlause.fintrack.feature.wallet.component

// FR-014, FR-017: a tap on a wallet row opens its detail, a long press opens the editor
internal data class WalletRowActions(
    val onOpen: () -> Unit,
    val onEdit: () -> Unit,
)
