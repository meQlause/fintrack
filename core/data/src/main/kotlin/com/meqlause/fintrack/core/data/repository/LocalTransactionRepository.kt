package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.mapper.toEntity
import com.meqlause.fintrack.core.data.mapper.toTransaction
import com.meqlause.fintrack.core.database.dao.TransactionDao
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** DOC-03, ADR-007, FR-030: money movements in Room (BR-001, BR-002). */
public class LocalTransactionRepository(
    private val transactionDao: TransactionDao,
) : TransactionRepository {
    override fun observeAll(): Flow<List<Transaction>> =
        transactionDao.observeAll().map { rows -> rows.map { it.toTransaction() } }

    override suspend fun insert(transaction: Transaction): TransactionId =
        TransactionId(transactionDao.insert(transaction.toEntity()))

    override suspend fun update(transaction: Transaction) {
        transactionDao.update(transaction.toEntity())
    }

    override suspend fun delete(transactionId: TransactionId) {
        transactionDao.delete(transactionId.value)
    }
}
