package com.meqlause.fintrack.di

import com.meqlause.fintrack.core.data.repository.LocalCategoryRepository
import com.meqlause.fintrack.core.data.repository.LocalInboxRepository
import com.meqlause.fintrack.core.data.repository.LocalInstallmentPlanRepository
import com.meqlause.fintrack.core.data.repository.LocalMerchantRuleRepository
import com.meqlause.fintrack.core.data.repository.LocalMonitoredAppRepository
import com.meqlause.fintrack.core.data.repository.LocalQuickEntryRepository
import com.meqlause.fintrack.core.data.repository.LocalSettingsRepository
import com.meqlause.fintrack.core.data.repository.LocalTransactionRepository
import com.meqlause.fintrack.core.data.repository.LocalWalletRepository
import com.meqlause.fintrack.core.data.repository.RoomAtomicRunner
import com.meqlause.fintrack.core.database.FintrackDatabase
import com.meqlause.fintrack.core.database.dao.CategoryDao
import com.meqlause.fintrack.core.database.dao.InboxItemDao
import com.meqlause.fintrack.core.database.dao.InstallmentPlanDao
import com.meqlause.fintrack.core.database.dao.MerchantRuleDao
import com.meqlause.fintrack.core.database.dao.MonitoredAppDao
import com.meqlause.fintrack.core.database.dao.QuickEntryDao
import com.meqlause.fintrack.core.database.dao.TransactionDao
import com.meqlause.fintrack.core.database.dao.WalletDao
import com.meqlause.fintrack.core.datastore.SettingsDataSource
import com.meqlause.fintrack.core.domain.repository.AtomicRunner
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.domain.repository.MerchantRuleRepository
import com.meqlause.fintrack.core.domain.repository.MonitoredAppRepository
import com.meqlause.fintrack.core.domain.repository.QuickEntryRepository
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
    fun provideQuickEntryRepository(quickEntryDao: QuickEntryDao): QuickEntryRepository =
        LocalQuickEntryRepository(quickEntryDao)

    @Provides
    @Singleton
    fun provideSettingsRepository(dataSource: SettingsDataSource): SettingsRepository =
        LocalSettingsRepository(dataSource)

    // FR-140: instalment plans, read by the credit wallet detail (FR-131)
    @Provides
    @Singleton
    fun provideInstallmentPlanRepository(installmentPlanDao: InstallmentPlanDao): InstallmentPlanRepository =
        LocalInstallmentPlanRepository(installmentPlanDao)

    // FR-084 … FR-091: detection pipeline storage
    @Provides
    @Singleton
    fun provideInboxRepository(inboxItemDao: InboxItemDao): InboxRepository = LocalInboxRepository(inboxItemDao)

    @Provides
    @Singleton
    fun provideMerchantRuleRepository(merchantRuleDao: MerchantRuleDao): MerchantRuleRepository =
        LocalMerchantRuleRepository(merchantRuleDao)

    @Provides
    @Singleton
    fun provideMonitoredAppRepository(monitoredAppDao: MonitoredAppDao): MonitoredAppRepository =
        LocalMonitoredAppRepository(monitoredAppDao)

    // BR-011: a confirmed item writes its transaction and status in one Room transaction
    @Provides
    @Singleton
    fun provideAtomicRunner(database: FintrackDatabase): AtomicRunner = RoomAtomicRunner(database)
}
