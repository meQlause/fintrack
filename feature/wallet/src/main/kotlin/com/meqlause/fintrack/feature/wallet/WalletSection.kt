package com.meqlause.fintrack.feature.wallet

// S-09: the wallets of one group (Tunai, Bank, ...), in manual order
internal data class WalletSection(
    val group: WalletGroupKey,
    val items: List<WalletItem>,
) {
    companion object {
        // S-09: the active wallets grouped in display order; empty groups are not shown
        fun group(active: List<WalletItem>): List<WalletSection> =
            WalletGroupKey.entries.mapNotNull { key ->
                val items = active.filter { WalletGroupKey.forType(it.wallet.kind.type) == key }
                items.takeIf { it.isNotEmpty() }?.let { WalletSection(group = key, items = it) }
            }
    }
}
