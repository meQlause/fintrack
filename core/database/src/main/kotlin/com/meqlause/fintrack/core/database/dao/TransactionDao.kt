package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.meqlause.fintrack.core.database.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: transaction writes are suspend, reads are Flow (03 threading). */
@Dao
interface TransactionDao {
    /** DOC-02: inserts a transaction and returns its generated id. */
    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    /** FR-034: overwrites the row with the same transactionId. */
    @Update
    suspend fun update(transaction: TransactionEntity)

    /** FR-035: removes the row with the given transactionId; an unknown id is ignored. */
    @Query("DELETE FROM `transaction` WHERE transactionId = :transactionId")
    suspend fun delete(transactionId: Long)

    /** DOC-02: every transaction in the order it happened. */
    @Query("SELECT * FROM `transaction` ORDER BY occurredAt")
    fun observeAll(): Flow<List<TransactionEntity>>

    /** DOC-11: removes every row before a restore. */
    @Query("DELETE FROM `transaction`")
    suspend fun deleteAll()
}
