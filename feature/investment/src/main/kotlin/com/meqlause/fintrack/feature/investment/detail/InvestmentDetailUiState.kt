package com.meqlause.fintrack.feature.investment.detail

import com.meqlause.fintrack.core.domain.investment.InvestmentDetail

// S-20: loaded is false until the first emission; valuation holds the digits of the open update sheet, if any
internal data class InvestmentDetailUiState(
    val loaded: Boolean = false,
    val detail: InvestmentDetail? = null,
    val valuation: String? = null,
)
