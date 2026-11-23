package com.meqlause.fintrack.core.domain.investment

import com.meqlause.fintrack.core.model.Wallet

/** FR-173, BR-051: an active INVESTMENT wallet with its valuation. */
public data class InvestmentAccount(
    public val wallet: Wallet,
    public val valuation: InvestmentValuation,
)
