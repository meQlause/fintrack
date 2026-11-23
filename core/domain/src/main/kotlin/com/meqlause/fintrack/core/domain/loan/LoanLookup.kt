package com.meqlause.fintrack.core.domain.loan

import com.meqlause.fintrack.core.domain.repository.LoanRepository
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanId
import kotlinx.coroutines.flow.first

// FR-155, FR-158: the stored loan with this id, or null when there is none
internal suspend fun LoanRepository.findLoan(loanId: LoanId): Loan? =
    observeAll().first().firstOrNull { it.loanId == loanId }
