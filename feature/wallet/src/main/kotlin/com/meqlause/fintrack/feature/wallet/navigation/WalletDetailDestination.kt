package com.meqlause.fintrack.feature.wallet.navigation

import kotlinx.serialization.Serializable

// FR-017, S-19: the wallet detail route; the wallet id is the only argument
@Serializable
public data class WalletDetailDestination(
    public val walletId: Long,
)
