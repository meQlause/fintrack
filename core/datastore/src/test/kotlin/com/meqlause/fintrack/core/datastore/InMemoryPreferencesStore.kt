package com.meqlause.fintrack.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// DOC-02: in-memory DataStore for tests that write more than once; the file store replaces its file with
// File.renameTo, which cannot overwrite an existing file on a Windows JVM host
internal class InMemoryPreferencesStore : DataStore<Preferences> {
    private val state = MutableStateFlow<Preferences>(emptyPreferences())
    private val writeLock = Mutex()

    override val data: Flow<Preferences> = state

    override suspend fun updateData(transform: suspend (current: Preferences) -> Preferences): Preferences =
        writeLock.withLock {
            val next = transform(state.value)
            state.value = next
            next
        }
}
