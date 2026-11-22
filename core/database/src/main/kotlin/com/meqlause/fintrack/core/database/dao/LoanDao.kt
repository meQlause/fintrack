package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.meqlause.fintrack.core.database.entity.LoanEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: loan writes are suspend, reads are Flow (03 threading). */
@Dao
interface LoanDao {
    /** FR-150: stores a loan agreement and returns its generated id. */
    @Insert
    suspend fun insert(loan: LoanEntity): Long

    /** DOC-02: every loan in creation order. */
    @Query("SELECT * FROM loan ORDER BY loanId")
    fun observeAll(): Flow<List<LoanEntity>>
}
