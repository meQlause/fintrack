package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.meqlause.fintrack.core.database.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: transaction writes are suspend, reads are Flow (03 threading). */
@Dao
interface TransactionDao {
    /** DOC-02: inserts a transaction and returns its generated id. */
    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    /** DOC-02: every transaction in the order it happened. */
    @Query("SELECT * FROM `transaction` ORDER BY occurredAt")
    fun observeAll(): Flow<List<TransactionEntity>>
}
