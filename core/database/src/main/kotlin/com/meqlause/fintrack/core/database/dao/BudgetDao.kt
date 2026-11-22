package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.meqlause.fintrack.core.database.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: budget writes are suspend, reads are Flow (03 threading). */
@Dao
interface BudgetDao {
    /** DOC-02: inserts a budget and returns its generated id; one budget per category (FR-074). */
    @Insert
    suspend fun insert(budget: BudgetEntity): Long

    /** DOC-02: every budget in creation order. */
    @Query("SELECT * FROM budget ORDER BY budgetId")
    fun observeAll(): Flow<List<BudgetEntity>>
}
