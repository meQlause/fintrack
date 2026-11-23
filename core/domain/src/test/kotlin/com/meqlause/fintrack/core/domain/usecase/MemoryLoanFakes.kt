package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.LoanRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

// FR-150: an in-memory loan table that keeps creation order, for the loan use case tests
internal class MemoryLoanRepository : LoanRepository {
    private val rows = MutableStateFlow<List<Loan>>(emptyList())
    private var lastId = 0L

    override fun observeAll(): Flow<List<Loan>> = rows

    override suspend fun insert(loan: Loan): LoanId {
        lastId += 1
        rows.value = rows.value + loan.copy(loanId = LoanId(lastId))
        return LoanId(lastId)
    }

    override suspend fun update(loan: Loan) {
        rows.value = rows.value.map { if (it.loanId == loan.loanId) loan else it }
    }

    override suspend fun delete(loanId: LoanId) {
        rows.value = rows.value.filterNot { it.loanId == loanId }
    }
}

// FR-150: an in-memory transaction table, so the tests can see the movements a loan writes
internal class MemoryTransactionRepository : TransactionRepository {
    private val rows = MutableStateFlow<List<Transaction>>(emptyList())
    private var lastId = 0L

    override fun observeAll(): Flow<List<Transaction>> = rows

    override suspend fun insert(transaction: Transaction): TransactionId {
        lastId += 1
        rows.value = rows.value + transaction.copy(transactionId = TransactionId(lastId))
        return TransactionId(lastId)
    }

    override suspend fun update(transaction: Transaction) {
        rows.value = rows.value.map { if (it.transactionId == transaction.transactionId) transaction else it }
    }

    override suspend fun delete(transactionId: TransactionId) {
        rows.value = rows.value.filterNot { it.transactionId == transactionId }
    }
}
