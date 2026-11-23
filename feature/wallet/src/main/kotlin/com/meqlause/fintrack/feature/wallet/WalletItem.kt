package com.meqlause.fintrack.feature.wallet

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet

// FR-014: one wallet with its computed balance (BR-004), as shown on a Dompet card
internal data class WalletItem(
    val wallet: Wallet,
    val balance: Money,
)
