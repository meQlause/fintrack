package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.MonitoredApp
import kotlinx.coroutines.flow.Flow

/** DOC-03, FR-090: access to the apps whose notifications are read; the package name is the key. */
public interface MonitoredAppRepository {
    /** DOC-02: every monitored app, by package name. */
    public fun observeAll(): Flow<List<MonitoredApp>>

    /** FR-090: registers an app; a second registration of the same package name is rejected. */
    public suspend fun insert(monitoredApp: MonitoredApp)
}
