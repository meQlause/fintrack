package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.Settings
import kotlinx.coroutines.flow.Flow

/** DOC-03, DOC-02: access to the app settings, which are kept in DataStore and not in Room. */
public interface SettingsRepository {
    /** DOC-02: the current settings, emitted again after every change. */
    public fun observe(): Flow<Settings>

    /** DOC-02: applies [transform] to the stored settings in one write. */
    public suspend fun update(transform: (Settings) -> Settings)
}
