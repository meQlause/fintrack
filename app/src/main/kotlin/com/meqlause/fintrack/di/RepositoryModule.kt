package com.meqlause.fintrack.di

import com.meqlause.fintrack.core.data.repository.LocalCategoryRepository
import com.meqlause.fintrack.core.data.repository.LocalSettingsRepository
import com.meqlause.fintrack.core.data.repository.LocalTransactionRepository
import com.meqlause.fintrack.core.data.repository.LocalWalletRepository
import com.meqlause.fintrack.core.database.dao.CategoryDao
import com.meqlause.fintrack.core.database.dao.TransactionDao
import com.meqlause.fintrack.core.database.dao.WalletDao
import com.meqlause.fintrack.core.datastore.SettingsDataSource
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** DOC-03, ADR-007: repository interfaces from core:domain, implemented by the Local repositories in core:data. */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideWalletRepository(walletDao: WalletDao): WalletRepository = LocalWalletRepository(walletDao)

    @Provides
    @Singleton
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository = LocalCategoryRepository(categoryDao)

    @Provides
    @Singleton
    fun provideTransactionRepository(transactionDao: TransactionDao): TransactionRepository =
        LocalTransactionRepository(transactionDao)

    @Provides
    @Singleton
    fun provideSettingsRepository(dataSource: SettingsDataSource): SettingsRepository =
        LocalSettingsRepository(dataSource)
}
