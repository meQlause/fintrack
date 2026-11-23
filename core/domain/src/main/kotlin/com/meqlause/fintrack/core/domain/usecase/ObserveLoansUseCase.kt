package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.loan.LoanEntry
import com.meqlause.fintrack.core.domain.repository.LoanRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

// FR-153: every loan with its own movements, so the list and the detail screens read one shape
public class ObserveLoansUseCase
    @Inject
    constructor(
        private val loans: LoanRepository,
        private val transactions: TransactionRepository,
    ) {
        /** FR-153, BR-040: loan movements are never counted by statistics; they only feed the balances here. */
        public operator fun invoke(): Flow<List<LoanEntry>> =
            combine(loans.observeAll(), transactions.observeAll()) { loanRows, rows ->
                loanRows.map { loan ->
                    val movements = rows.filter { it.routing.loanId == loan.loanId }
                    LoanEntry(loan, movements.sortedBy { it.timing.occurredAt })
                }
            }
    }
