package com.meqlause.fintrack.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import com.meqlause.fintrack.core.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences as StoredPreferences

/** DOC-02: the single access point to the app settings kept in DataStore, not Room. */
public class SettingsDataSource(
    private val dataStore: DataStore<StoredPreferences>,
) {
    /** DOC-02: the current settings, emitted again after every change. */
    public val settings: Flow<Settings> = dataStore.data.map { it.toSettings() }

    /** DOC-02: reads the stored settings, applies [transform] and stores the result in one edit. */
    public suspend fun update(transform: (Settings) -> Settings) {
        dataStore.edit { prefs -> prefs.writeSettings(transform(prefs.toSettings())) }
    }
}
