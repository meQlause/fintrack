package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/** ADR-007, DOC-02: in-memory settings that start at the DOC-02 defaults. */
public class FakeSettingsRepository : SettingsRepository {
    private val settings = MutableStateFlow(Settings())

    override fun observe(): Flow<Settings> = settings

    override suspend fun update(transform: (Settings) -> Settings) {
        settings.update(transform)
    }
}
