package com.meqlause.fintrack.feature.loan.detail

import com.meqlause.fintrack.core.domain.loan.LoanEntry
import com.meqlause.fintrack.core.domain.loan.RepaymentError
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Wallet

// S-17: everything the loan detail shows; the ViewModel owns it and the screen only reads it
internal data class LoanDetailUiState(
    val entry: LoanEntry? = null,
    val isLoaded: Boolean = false,
    val wallets: List<Wallet> = emptyList(),
    val sheet: LoanDetailSheet? = null,
    val dialog: LoanDialog? = null,
    val repayment: RepaymentForm = RepaymentForm(),
    val errors: Set<RepaymentError> = emptySet(),
    val finished: Boolean = false,
) {
    // FR-152: a repayment is possible while the loan is open
    val canRepay: Boolean
        get() = entry?.loan?.status == LoanStatus.OPEN

    // FR-155: only an open piutang can be written off
    val canWriteOff: Boolean
        get() = canRepay && entry?.loan?.agreement?.direction == LoanDirection.LENT
}
