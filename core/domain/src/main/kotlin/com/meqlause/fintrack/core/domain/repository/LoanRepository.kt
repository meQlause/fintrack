package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanId
import kotlinx.coroutines.flow.Flow

/** DOC-03, FR-150: access to loans to and from people (BR-041, BR-042). */
public interface LoanRepository {
    /** DOC-02: every loan in creation order. */
    public fun observeAll(): Flow<List<Loan>>

    /** FR-150: stores a loan agreement and returns its generated id; the loanId of [loan] is ignored. */
    public suspend fun insert(loan: Loan): LoanId
}
