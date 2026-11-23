package com.meqlause.fintrack.core.domain.budget

import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.model.Preferences
import com.meqlause.fintrack.core.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// FR-050: settings held in memory, with the period start day given by the test
internal class InMemorySettingsRepository(
    startDay: Int = 1,
) : SettingsRepository {
    private val settings = MutableStateFlow(Settings(preferences = Preferences(periodStartDay = startDay)))

    override fun observe(): Flow<Settings> = settings

    override suspend fun update(transform: (Settings) -> Settings) {
        settings.update(transform)
    }
}
