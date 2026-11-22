package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.datastore.SettingsDataSource
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.model.Settings
import kotlinx.coroutines.flow.Flow

/** DOC-03, ADR-007, DOC-02: settings in DataStore, read and written through the datastore data source. */
public class LocalSettingsRepository(
    private val dataSource: SettingsDataSource,
) : SettingsRepository {
    override fun observe(): Flow<Settings> = dataSource.settings

    override suspend fun update(transform: (Settings) -> Settings) {
        dataSource.update(transform)
    }
}
