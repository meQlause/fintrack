package com.meqlause.fintrack.di

import android.content.Context
import com.meqlause.fintrack.core.backup.ContentBackupFiles
import com.meqlause.fintrack.core.common.coroutines.IoDispatcher
import com.meqlause.fintrack.core.data.repository.LocalBackupStore
import com.meqlause.fintrack.core.database.FintrackDatabase
import com.meqlause.fintrack.core.domain.port.BackupFiles
import com.meqlause.fintrack.core.domain.port.BackupStore
import com.meqlause.fintrack.core.domain.repository.BackupRepository
import com.meqlause.fintrack.core.domain.repository.TransactionCsvRepository
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
internal object BackupModule {
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
    fun provideCsvRepository(factory: CsvRepositoryFactory): TransactionCsvRepository = factory.create()

    @Provides
    @Singleton
    fun provideBackupRepository(factory: BackupRepositoryFactory): BackupRepository = factory.create()
}
