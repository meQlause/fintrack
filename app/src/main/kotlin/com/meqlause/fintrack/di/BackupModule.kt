package com.meqlause.fintrack.di

import android.content.Context
import com.meqlause.fintrack.core.backup.ContentBackupFiles
import com.meqlause.fintrack.core.backup.FileBackupRepository
import com.meqlause.fintrack.core.common.coroutines.IoDispatcher
import com.meqlause.fintrack.core.common.time.ClockProvider
import com.meqlause.fintrack.core.data.repository.LocalBackupStore
import com.meqlause.fintrack.core.database.FintrackDatabase
import com.meqlause.fintrack.core.domain.repository.BackupFiles
import com.meqlause.fintrack.core.domain.repository.BackupRepository
import com.meqlause.fintrack.core.domain.repository.BackupStore
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

// DOC-11, FR-110, FR-111: wires the encrypted backup service and its file, store and I/O collaborators
@Module
@InstallIn(SingletonComponent::class)
object BackupModule {
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideBackupStore(database: FintrackDatabase): BackupStore = LocalBackupStore(database)

    @Provides
    @Singleton
    fun provideBackupFiles(
        @ApplicationContext context: Context,
    ): BackupFiles = ContentBackupFiles(context)

    @Provides
    @Singleton
    fun provideBackupRepository(
        store: BackupStore,
        settings: SettingsRepository,
        files: BackupFiles,
        clock: ClockProvider,
        @IoDispatcher io: CoroutineDispatcher,
        @ApplicationContext context: Context,
    ): BackupRepository =
        FileBackupRepository(
            store = store,
            settings = settings,
            files = files,
            clock = clock,
            io = io,
            appVersionName =
                context.packageManager
                    .getPackageInfo(context.packageName, 0)
                    .versionName
                    .orEmpty(),
        )
}
