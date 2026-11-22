package com.meqlause.fintrack.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.meqlause.fintrack.core.common.coroutines.ApplicationScope
import com.meqlause.fintrack.core.datastore.SettingsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Singleton

/** DOC-02, DOC-03: the DataStore file that holds the app settings, outside Room. */
@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
    private const val SETTINGS_FILE = "fintrack_settings.preferences_pb"

    @Provides
    @Singleton
    fun provideSettingsStore(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope,
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(scope = scope) { File(context.filesDir, SETTINGS_FILE) }

    @Provides
    @Singleton
    fun provideSettingsDataSource(store: DataStore<Preferences>): SettingsDataSource = SettingsDataSource(store)
}
