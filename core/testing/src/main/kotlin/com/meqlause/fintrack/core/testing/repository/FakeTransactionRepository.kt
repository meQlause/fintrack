package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import kotlinx.coroutines.flow.Flow

/** ADR-007, FR-030: in-memory transactions ordered by the time they happened. */
public class FakeTransactionRepository : TransactionRepository {
    private val store =
        RowStore<Transaction>(
            idOf = { it.transactionId.value },
            withId = { transaction, newId -> transaction.copy(transactionId = TransactionId(newId)) },
            order = compareBy { it.timing.occurredAt },
        )

    override fun observeAll(): Flow<List<Transaction>> = store.observe()

    override suspend fun insert(transaction: Transaction): TransactionId = TransactionId(store.append(transaction))

    override suspend fun update(transaction: Transaction) {
        store.replace(transaction)
    }
}
