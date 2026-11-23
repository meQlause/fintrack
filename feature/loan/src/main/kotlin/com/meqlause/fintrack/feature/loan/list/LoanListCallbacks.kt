package com.meqlause.fintrack.feature.loan.list

import com.meqlause.fintrack.core.model.LoanDirection

// S-17: what the loan list asks its route to do; the screen itself keeps no state
internal data class LoanListCallbacks(
    val onBack: () -> Unit,
    val onPickDirection: (LoanDirection) -> Unit,
    val onOpenLoan: (Long) -> Unit,
)
