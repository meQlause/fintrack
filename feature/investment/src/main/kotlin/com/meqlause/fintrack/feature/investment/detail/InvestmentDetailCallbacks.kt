package com.meqlause.fintrack.feature.investment.detail

// S-20: every action of the investment detail, wired to the ViewModel by InvestmentDetailRoute
internal data class InvestmentDetailCallbacks(
    val onBack: () -> Unit,
    val onOpenValuation: () -> Unit,
    val onDigit: (String) -> Unit,
    val onBackspace: () -> Unit,
    val onSaveValuation: () -> Unit,
    val onDismissValuation: () -> Unit,
    val onTransfer: () -> Unit,
)
