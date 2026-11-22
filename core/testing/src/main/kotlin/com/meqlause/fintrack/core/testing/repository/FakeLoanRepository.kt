package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.LoanRepository
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanId
import kotlinx.coroutines.flow.Flow

/** ADR-007, FR-150: in-memory loans in creation order. */
public class FakeLoanRepository : LoanRepository {
    private val store =
        RowStore<Loan>(
            idOf = { it.loanId.value },
            withId = { loan, newId -> loan.copy(loanId = LoanId(newId)) },
            order = compareBy { it.loanId.value },
        )

    override fun observeAll(): Flow<List<Loan>> = store.observe()

    override suspend fun insert(loan: Loan): LoanId = LoanId(store.append(loan))
}
