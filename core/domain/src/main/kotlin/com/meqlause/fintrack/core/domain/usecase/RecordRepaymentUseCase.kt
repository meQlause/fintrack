package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.loan.LoanEntry
import com.meqlause.fintrack.core.domain.loan.LoanTransactionFactory
import com.meqlause.fintrack.core.domain.loan.RepaymentError
import com.meqlause.fintrack.core.domain.loan.RepaymentResult
import com.meqlause.fintrack.core.domain.loan.RepaymentRules
import com.meqlause.fintrack.core.domain.loan.findLoan
import com.meqlause.fintrack.core.domain.loan.repaymentType
import com.meqlause.fintrack.core.domain.repository.AtomicRunner
import com.meqlause.fintrack.core.domain.repository.LoanRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

// FR-152, FR-155, BR-011, BR-041: a repayment is stored, and the loan settles in the same write when it reaches zero
public class RecordRepaymentUseCase
    @Inject
    constructor(
        private val loans: LoanRepository,
        private val transactions: TransactionRepository,
        private val atomic: AtomicRunner,
        private val clock: Clock,
    ) {
        /** FR-152: nothing is stored when the amount is above the outstanding balance or the loan is closed. */
        public suspend operator fun invoke(
            loanId: LoanId,
            amount: Money,
            walletId: WalletId?,
        ): RepaymentResult {
            val entry = entryOf(loanId) ?: return RepaymentResult.Rejected(setOf(RepaymentError.LOAN_NOT_FOUND))
            val errors = RepaymentRules.check(amount, walletId, entry)
            if (errors.isNotEmpty()) return RepaymentResult.Rejected(errors)
            return atomic.atomically { store(entry, amount, requireNotNull(walletId)) }
        }

        private suspend fun entryOf(loanId: LoanId): LoanEntry? {
            val loan = loans.findLoan(loanId) ?: return null
            val movements = transactions.observeAll().first().filter { it.routing.loanId == loanId }
            return LoanEntry(loan, movements.sortedBy { it.timing.occurredAt })
        }

        private suspend fun store(
            entry: LoanEntry,
            amount: Money,
            walletId: WalletId,
        ): RepaymentResult.Saved {
            val now = Instant.now(clock)
            val loan = entry.loan
            val movement =
                LoanTransactionFactory.movement(
                    type = loan.agreement.direction.repaymentType(),
                    amount = amount,
                    walletId = walletId,
                    loanId = loan.loanId,
                    note = "",
                    occurredAt = now,
                    now = now,
                )
            val id = transactions.insert(movement)
            if ((entry.balance.repaid + amount) >= entry.balance.principal) settle(loan, now)
            return RepaymentResult.Saved(id)
        }

        // BR-041: outstanding 0 → SETTLED with closedAt = now
        private suspend fun settle(
            loan: Loan,
            now: Instant,
        ) {
            loans.update(
                loan.copy(status = LoanStatus.SETTLED, closedAt = now, audit = loan.audit.copy(updatedAt = now)),
            )
        }
    }
