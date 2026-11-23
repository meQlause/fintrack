package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.MonitoredApp
import kotlinx.coroutines.flow.Flow
import java.time.Instant

/** DOC-03, FR-090: access to the apps whose notifications are read; the package name is the key. */
public interface MonitoredAppRepository {
    /** DOC-02: every monitored app, by package name. */
    public fun observeAll(): Flow<List<MonitoredApp>>

    /** FR-090: registers an app; a second registration of the same package name is rejected. */
    public suspend fun insert(monitoredApp: MonitoredApp)

    /** FR-090: the registered app with this package name, or null when it is not monitored. */
    public suspend fun find(packageName: String): MonitoredApp?

    /** FR-090: stamps the time the last notification from the app was received. */
    public suspend fun recordNotification(
        packageName: String,
        receivedAt: Instant,
    )
}
