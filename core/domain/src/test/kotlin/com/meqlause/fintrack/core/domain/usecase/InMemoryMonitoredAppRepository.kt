package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.MonitoredAppRepository
import com.meqlause.fintrack.core.model.MonitoredApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant

// FR-090: in-memory monitored apps keyed by package name
internal class InMemoryMonitoredAppRepository : MonitoredAppRepository {
    private val rows = MutableStateFlow<List<MonitoredApp>>(emptyList())

    override fun observeAll(): Flow<List<MonitoredApp>> = rows

    override suspend fun insert(monitoredApp: MonitoredApp) {
        require(rows.value.none { it.packageName == monitoredApp.packageName }) { "unique constraint violated" }
        rows.value = rows.value + monitoredApp
    }

    override suspend fun upsert(monitoredApp: MonitoredApp) {
        rows.value = rows.value.filterNot { it.packageName == monitoredApp.packageName } + monitoredApp
    }

    override suspend fun find(packageName: String): MonitoredApp? =
        rows.value.firstOrNull { it.packageName == packageName }

    override suspend fun recordNotification(
        packageName: String,
        receivedAt: Instant,
    ) {
        rows.value =
            rows.value.map { if (it.packageName == packageName) it.copy(lastNotificationAt = receivedAt) else it }
    }
}
