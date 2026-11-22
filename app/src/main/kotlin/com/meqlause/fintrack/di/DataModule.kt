package com.meqlause.fintrack.di

import android.content.Context
import com.meqlause.fintrack.core.data.repository.LocalCategoryRepository
import com.meqlause.fintrack.core.database.FintrackDatabase
import com.meqlause.fintrack.core.database.FintrackDatabaseFactory
import com.meqlause.fintrack.core.database.dao.CategoryDao
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.security.AndroidKeystoreWrapper
import com.meqlause.fintrack.core.security.DatabaseKeyProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// NFR-003, ADR-009: app-level data graph; the encrypted database is opened once and shared by every repository
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    // NFR-003: the SQLCipher passphrase is unwrapped from the Keystore before the database opens
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): FintrackDatabase {
        val keys = DatabaseKeyProvider(context.filesDir, AndroidKeystoreWrapper())
        return FintrackDatabaseFactory.open(context, keys.passphrase())
    }

    @Provides
    fun provideCategoryDao(database: FintrackDatabase): CategoryDao = database.categoryDao()

    // DOC-03, FR-020: categories are served from the Room-backed repository
    @Provides
    @Singleton
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository = LocalCategoryRepository(categoryDao)
}
