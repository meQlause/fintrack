package com.meqlause.fintrack.feature.investment

import com.meqlause.fintrack.core.domain.investment.InvestmentPortfolio

// S-08: portfolio is null while loading; draft is the open creation sheet, if any
internal data class InvestmentUiState(
    val portfolio: InvestmentPortfolio? = null,
    val draft: InvestmentDraft? = null,
)
