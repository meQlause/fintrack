package com.meqlause.fintrack.feature.investment.detail

import com.meqlause.fintrack.feature.investment.AmountSheetCallbacks

// S-20: every action of the investment detail, wired to the ViewModel by InvestmentDetailRoute
internal data class InvestmentDetailCallbacks(
    val onBack: () -> Unit,
    val onOpenValuation: () -> Unit,
    val onTransfer: () -> Unit,
    val valuation: AmountSheetCallbacks,
)
