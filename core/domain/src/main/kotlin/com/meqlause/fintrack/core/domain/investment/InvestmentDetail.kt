package com.meqlause.fintrack.core.domain.investment

/** FR-173: one investment account with its full timeline, newest first. */
public data class InvestmentDetail(
    public val account: InvestmentAccount,
    public val activity: List<InvestmentActivity>,
)
