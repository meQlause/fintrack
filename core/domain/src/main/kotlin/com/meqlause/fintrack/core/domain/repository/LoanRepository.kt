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

    /** FR-155, FR-158: stores the changed fields of an existing loan; its loanId selects the row. */
    public suspend fun update(loan: Loan)

    /** BR-046: removes a loan; its repayments and opening transaction must already be gone. */
    public suspend fun delete(loanId: LoanId)
}
