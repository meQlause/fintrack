package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.loan.findLoan
import com.meqlause.fintrack.core.domain.repository.LoanRepository
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

// FR-158: the due date of an open loan can be changed or cleared (null) from the loan detail
public class ChangeLoanDueDateUseCase
    @Inject
    constructor(
        private val loans: LoanRepository,
        private val clock: Clock,
    ) {
        /** FR-158: returns false, and changes nothing, when the loan is missing or no longer open. */
        public suspend operator fun invoke(
            loanId: LoanId,
            dueDate: LocalDate?,
        ): Boolean {
            val loan = loans.findLoan(loanId) ?: return false
            if (loan.status != LoanStatus.OPEN) return false
            val now = Instant.now(clock)
            val due = dueDate?.atStartOfDay(clock.zone)?.toInstant()
            loans.update(
                loan.copy(agreement = loan.agreement.copy(dueDate = due), audit = loan.audit.copy(updatedAt = now)),
            )
            return true
        }
    }
