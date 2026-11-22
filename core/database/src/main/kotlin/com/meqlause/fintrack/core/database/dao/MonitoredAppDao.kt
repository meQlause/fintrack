package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.meqlause.fintrack.core.database.entity.MonitoredAppEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: monitored app writes are suspend, reads are Flow (03 threading). */
@Dao
interface MonitoredAppDao {
    /** FR-090: registers a notification source keyed by its package name. */
    @Insert
    suspend fun insert(monitoredApp: MonitoredAppEntity)

    /** DOC-02: every monitored app, by package name. */
    @Query("SELECT * FROM monitored_app ORDER BY packageName")
    fun observeAll(): Flow<List<MonitoredAppEntity>>
}
