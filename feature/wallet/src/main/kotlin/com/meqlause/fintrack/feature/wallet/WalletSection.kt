package com.meqlause.fintrack.feature.wallet

// S-09: the wallets of one group (Tunai, Bank, ...), in manual order
internal data class WalletSection(
    val group: WalletGroupKey,
    val items: List<WalletItem>,
)
