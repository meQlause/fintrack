package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.mapper.toEntity
import com.meqlause.fintrack.core.data.mapper.toMonitoredApp
import com.meqlause.fintrack.core.database.dao.MonitoredAppDao
import com.meqlause.fintrack.core.domain.repository.MonitoredAppRepository
import com.meqlause.fintrack.core.model.MonitoredApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

/** DOC-03, ADR-007, FR-090: monitored apps in Room, keyed by package name. */
public class LocalMonitoredAppRepository(
    private val monitoredAppDao: MonitoredAppDao,
) : MonitoredAppRepository {
    override fun observeAll(): Flow<List<MonitoredApp>> =
        monitoredAppDao.observeAll().map { rows -> rows.map { it.toMonitoredApp() } }

    override suspend fun insert(monitoredApp: MonitoredApp) {
        monitoredAppDao.insert(monitoredApp.toEntity())
    }

    override suspend fun find(packageName: String): MonitoredApp? = monitoredAppDao.find(packageName)?.toMonitoredApp()

    override suspend fun recordNotification(
        packageName: String,
        receivedAt: Instant,
    ) {
        monitoredAppDao.recordNotification(packageName, receivedAt.toEpochMilli())
    }
}
