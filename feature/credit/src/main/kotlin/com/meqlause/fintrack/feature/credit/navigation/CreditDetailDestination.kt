package com.meqlause.fintrack.feature.credit.navigation

import kotlinx.serialization.Serializable

// FR-131, S-14: the credit wallet detail route; the wallet id is the only argument
@Serializable
public data class CreditDetailDestination(
    public val walletId: Long,
)
