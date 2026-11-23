package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.loan.LoanDraft
import com.meqlause.fintrack.core.domain.loan.LoanRules
import com.meqlause.fintrack.core.domain.loan.LoanTransactionFactory
import com.meqlause.fintrack.core.domain.loan.openingType
import com.meqlause.fintrack.core.domain.port.AtomicRunner
import com.meqlause.fintrack.core.domain.repository.LoanRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.transaction.SaveTransactionResult
import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

/** FR-150, FR-151, BR-011: a loan and its opening LEND or BORROW movement are stored together, or not at all */
public class SaveLoanUseCase
    @Inject
    constructor(
        private val loans: LoanRepository,
        private val transactions: TransactionRepository,
        private val atomic: AtomicRunner,
        private val clock: Clock,
    ) {
        /** FR-150: nothing is stored when a rule is broken; the rule errors come back instead. */
        public suspend operator fun invoke(draft: LoanDraft): SaveTransactionResult {
            val errors = LoanRules.check(draft)
            if (errors.isNotEmpty()) return SaveTransactionResult.Rejected(errors)
            return atomic.atomically { store(draft) }
        }

        private suspend fun store(draft: LoanDraft): SaveTransactionResult.Saved {
            val nowInstant = Instant.now(clock)
            val loanId = loans.insert(loanOf(draft, nowInstant))
            val walletId = requireNotNull(draft.walletId) { "LoanRules guarantees a wallet" }
            val movement =
                LoanTransactionFactory.movement(
                    movement = Movement(draft.direction.openingType(), draft.principal),
                    routing = Routing(walletId, null, null, loanId),
                    memo = Memo(note = draft.note.trim()),
                    timing = Timing(draft.startDate, Audit(nowInstant, nowInstant)),
                )
            return SaveTransactionResult.Saved(transactions.insert(movement))
        }

        // FR-150: the loan starts OPEN; a due date is stored as the start of that day in the clock's zone
        private fun loanOf(
            draft: LoanDraft,
            currentTime: Instant,
        ): Loan =
            Loan(
                loanId = LoanId(0L),
                agreement =
                    Agreement(
                        direction = draft.direction,
                        personName = draft.personName.trim(),
                        principal = draft.principal,
                        startDate = draft.startDate,
                        dueDate = dueInstant(draft.dueDate),
                        note = draft.note.trim(),
                    ),
                status = LoanStatus.OPEN,
                closedAt = null,
                audit = Audit(createdAt = currentTime, updatedAt = currentTime),
            )

        private fun dueInstant(date: LocalDate?): Instant? = date?.atStartOfDay(clock.zone)?.toInstant()
    }
