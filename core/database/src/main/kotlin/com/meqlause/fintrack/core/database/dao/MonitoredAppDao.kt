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

    /** FR-090: the monitored app with this package name, or null when it is not registered. */
    @Query("SELECT * FROM monitored_app WHERE packageName = :packageName LIMIT 1")
    suspend fun find(packageName: String): MonitoredAppEntity?

    /** FR-090, FR-082: stamps the time of the last notification received from the app. */
    @Query("UPDATE monitored_app SET lastNotificationAt = :receivedAt WHERE packageName = :packageName")
    suspend fun recordNotification(
        packageName: String,
        receivedAt: Long,
    )

    /** DOC-02: every monitored app, by package name. */
    @Query("SELECT * FROM monitored_app ORDER BY packageName")
    fun observeAll(): Flow<List<MonitoredAppEntity>>
}
