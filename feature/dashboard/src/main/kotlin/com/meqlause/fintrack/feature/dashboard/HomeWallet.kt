package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId

// FR-040: one wallet chip under the hero, balance already computed by the repository (BR-043)
internal data class HomeWallet(
    val walletId: WalletId,
    val name: String,
    val balance: Money,
    val isCredit: Boolean = false,
    val appearance: Appearance = Appearance("wallet", "gray"),
)
