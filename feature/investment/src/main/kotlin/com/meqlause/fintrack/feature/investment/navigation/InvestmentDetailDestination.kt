package com.meqlause.fintrack.feature.investment.navigation

import kotlinx.serialization.Serializable

// FR-173, S-20: the investment detail route; the wallet id is the only argument
@Serializable
public data class InvestmentDetailDestination(
    public val walletId: Long,
)
