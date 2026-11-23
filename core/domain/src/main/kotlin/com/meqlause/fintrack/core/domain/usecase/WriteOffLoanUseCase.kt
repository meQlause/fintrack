package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.loan.findLoan
import com.meqlause.fintrack.core.domain.repository.LoanRepository
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

// FR-155, BR-042: a LENT loan that is still open can be written off; no money moves and statistics stay unchanged
public class WriteOffLoanUseCase
    @Inject
    constructor(
        private val loans: LoanRepository,
        private val clock: Clock,
    ) {
        /** BR-042: returns false, and changes nothing, when the loan is not an open piutang. */
        public suspend operator fun invoke(loanId: LoanId): Boolean {
            val loan = loans.findLoan(loanId) ?: return false
            if (loan.agreement.direction != LoanDirection.LENT || loan.status != LoanStatus.OPEN) return false
            val now = Instant.now(clock)
            loans.update(
                loan.copy(status = LoanStatus.WRITTEN_OFF, closedAt = now, audit = loan.audit.copy(updatedAt = now)),
            )
            return true
        }
    }
