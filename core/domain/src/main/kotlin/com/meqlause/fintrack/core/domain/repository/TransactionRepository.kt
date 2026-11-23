package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import kotlinx.coroutines.flow.Flow

/** DOC-03, FR-030: access to money movements (BR-001, BR-002). */
public interface TransactionRepository {
    /** DOC-02: every transaction in the order it happened. */
    public fun observeAll(): Flow<List<Transaction>>

    /** FR-030: stores a transaction and returns its generated id; the transactionId of [transaction] is ignored. */
    public suspend fun insert(transaction: Transaction): TransactionId

    /** FR-034: stores the changed fields of an existing transaction; transactionId selects the row. */
    public suspend fun update(transaction: Transaction)
}
