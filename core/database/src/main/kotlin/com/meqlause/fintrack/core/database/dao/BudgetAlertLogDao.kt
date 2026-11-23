package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.meqlause.fintrack.core.database.entity.BudgetAlertLogEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: alert log writes are suspend, reads are Flow (03 threading). */
@Dao
interface BudgetAlertLogDao {
    /** FR-073: records a sent alert; the composite key stops the same threshold firing twice in a period. */
    @Insert
    suspend fun insert(alert: BudgetAlertLogEntity)

    /** DOC-02: every sent alert, oldest first. */
    @Query("SELECT * FROM budget_alert_log ORDER BY notifiedAt")
    fun observeAll(): Flow<List<BudgetAlertLogEntity>>

    /** DOC-11: removes every row before a restore. */
    @Query("DELETE FROM budget_alert_log")
    suspend fun deleteAll()
}
