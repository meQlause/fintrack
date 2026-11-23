package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.mapper.toEntity
import com.meqlause.fintrack.core.data.mapper.toLoan
import com.meqlause.fintrack.core.database.dao.LoanDao
import com.meqlause.fintrack.core.domain.repository.LoanRepository
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** DOC-03, ADR-007, FR-150: loans in Room; repayments are transactions that carry the loan id (ADR-016). */
public class LocalLoanRepository(
    private val loanDao: LoanDao,
) : LoanRepository {
    override fun observeAll(): Flow<List<Loan>> = loanDao.observeAll().map { rows -> rows.map { it.toLoan() } }

    override suspend fun insert(loan: Loan): LoanId = LoanId(loanDao.insert(loan.toEntity()))

    override suspend fun update(loan: Loan) {
        loanDao.update(loan.toEntity())
    }

    override suspend fun delete(loanId: LoanId) {
        loanDao.delete(loanId.value)
    }
}
