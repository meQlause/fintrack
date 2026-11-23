package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.MonitoredAppRepository
import com.meqlause.fintrack.core.model.MonitoredApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.Instant

/** ADR-007, FR-090: in-memory monitored apps keyed by package name. */
public class FakeMonitoredAppRepository : MonitoredAppRepository {
    private val apps = MutableStateFlow<List<MonitoredApp>>(emptyList())

    override fun observeAll(): Flow<List<MonitoredApp>> = apps.map { list -> list.sortedBy { it.packageName } }

    override suspend fun insert(monitoredApp: MonitoredApp) {
        require(apps.value.none { it.packageName == monitoredApp.packageName }) { "unique constraint violated" }
        apps.update { it + monitoredApp }
    }

    override suspend fun find(packageName: String): MonitoredApp? =
        apps.value.firstOrNull { it.packageName == packageName }

    override suspend fun recordNotification(
        packageName: String,
        receivedAt: Instant,
    ) {
        apps.update { list ->
            list.map { if (it.packageName == packageName) it.copy(lastNotificationAt = receivedAt) else it }
        }
    }
}
