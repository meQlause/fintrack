package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.meqlause.fintrack.core.database.entity.InstallmentPlanEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: installment writes are suspend, reads are Flow (03 threading). */
@Dao
interface InstallmentPlanDao {
    /** FR-140: stores an installment plan and returns its generated id. */
    @Insert
    suspend fun insert(plan: InstallmentPlanEntity): Long

    /** DOC-02: every installment plan in creation order. */
    @Query("SELECT * FROM installment_plan ORDER BY installmentPlanId")
    fun observeAll(): Flow<List<InstallmentPlanEntity>>
}
