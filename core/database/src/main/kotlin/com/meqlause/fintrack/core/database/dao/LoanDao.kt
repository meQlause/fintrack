package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.meqlause.fintrack.core.database.entity.LoanEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: loan writes are suspend, reads are Flow (03 threading). */
@Dao
interface LoanDao {
    /** FR-150: stores a loan agreement and returns its generated id. */
    @Insert
    suspend fun insert(loan: LoanEntity): Long

    /** FR-155, FR-158: stores the changed fields of a loan; loanId selects the row. */
    @Update
    suspend fun update(loan: LoanEntity)

    /** BR-046: removes a loan row; its transactions must be removed first (RESTRICT foreign key). */
    @Query("DELETE FROM loan WHERE loanId = :loanId")
    suspend fun delete(loanId: Long)

    /** DOC-02: every loan in creation order. */
    @Query("SELECT * FROM loan ORDER BY loanId")
    fun observeAll(): Flow<List<LoanEntity>>
}
