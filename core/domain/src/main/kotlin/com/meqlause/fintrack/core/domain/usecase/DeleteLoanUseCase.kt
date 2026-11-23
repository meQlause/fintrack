package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.port.AtomicRunner
import com.meqlause.fintrack.core.domain.repository.LoanRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.model.LoanId
import kotlinx.coroutines.flow.first
import javax.inject.Inject

// BR-046, BR-011: deleting a loan removes its opening movement and every repayment in the same write
public class DeleteLoanUseCase
    @Inject
    constructor(
        private val loans: LoanRepository,
        private val transactions: TransactionRepository,
        private val atomic: AtomicRunner,
    ) {
        /** BR-046: the loan is removed only together with the transactions that carry its id. */
        public suspend operator fun invoke(loanId: LoanId) {
            atomic.atomically {
                transactions
                    .observeAll()
                    .first()
                    .filter { it.routing.loanId == loanId }
                    .forEach { transactions.delete(it.transactionId) }
                loans.delete(loanId)
            }
        }
    }
