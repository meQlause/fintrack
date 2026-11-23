package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** FR-030, FR-034: in-memory transactions for the domain tests; [stored] lists what was inserted or updated. */
internal class InMemoryTransactionRepository : TransactionRepository {
    private val rows = MutableStateFlow<List<Transaction>>(emptyList())
    private var lastId = 0L

    val stored: List<Transaction>
        get() = rows.value

    override fun observeAll(): Flow<List<Transaction>> = rows

    override suspend fun insert(transaction: Transaction): TransactionId {
        lastId += 1
        val id = TransactionId(lastId)
        rows.value = rows.value + transaction.copy(transactionId = id)
        return id
    }

    override suspend fun update(transaction: Transaction) {
        rows.value = rows.value.map { if (it.transactionId == transaction.transactionId) transaction else it }
    }

    override suspend fun delete(transactionId: TransactionId) {
        rows.value = rows.value.filterNot { it.transactionId == transactionId }
    }
}
