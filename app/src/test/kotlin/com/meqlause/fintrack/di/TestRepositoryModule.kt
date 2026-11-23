package com.meqlause.fintrack.di

import com.meqlause.fintrack.core.domain.repository.AtomicRunner
import com.meqlause.fintrack.core.domain.repository.BudgetAlertLogRepository
import com.meqlause.fintrack.core.domain.repository.BudgetRepository
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.domain.repository.LoanRepository
import com.meqlause.fintrack.core.domain.repository.MerchantRuleRepository
import com.meqlause.fintrack.core.domain.repository.MonitoredAppRepository
import com.meqlause.fintrack.core.domain.repository.QuickEntryRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.ValuationRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.testing.repository.FakeBudgetAlertLogRepository
import com.meqlause.fintrack.core.testing.repository.FakeBudgetRepository
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
import com.meqlause.fintrack.core.testing.repository.FakeInboxRepository
import com.meqlause.fintrack.core.testing.repository.FakeInstallmentPlanRepository
import com.meqlause.fintrack.core.testing.repository.FakeLoanRepository
import com.meqlause.fintrack.core.testing.repository.FakeMerchantRuleRepository
import com.meqlause.fintrack.core.testing.repository.FakeMonitoredAppRepository
import com.meqlause.fintrack.core.testing.repository.FakeQuickEntryRepository
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeValuationRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.repository.PassthroughAtomicRunner
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

// DOC-06: UI tests run without the Android Keystore; the repositories are in-memory fakes with onboarding already done
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])
object TestRepositoryModule {
    // The wallet fake computes balances from the transaction fake, so both must share one instance
    @Provides
    @Singleton
    fun provideFakeTransactionRepository(): FakeTransactionRepository = FakeTransactionRepository()

    @Provides
    @Singleton
    fun provideWalletRepository(transactions: FakeTransactionRepository): WalletRepository =
        FakeWalletRepository(transactions)

    @Provides
    @Singleton
    fun provideCategoryRepository(): CategoryRepository = FakeCategoryRepository()

    @Provides
    @Singleton
    fun provideTransactionRepository(transactions: FakeTransactionRepository): TransactionRepository = transactions

    @Provides
    @Singleton
    fun provideQuickEntryRepository(): QuickEntryRepository = FakeQuickEntryRepository()

    @Provides
    @Singleton
    fun provideSettingsRepository(): SettingsRepository {
        val settings = FakeSettingsRepository()
        runBlocking {
            settings.update { current ->
                current.copy(preferences = current.preferences.copy(onboardingCompleted = true))
            }
        }
        return settings
    }

    @Provides
    @Singleton
    fun provideInstallmentPlanRepository(): InstallmentPlanRepository = FakeInstallmentPlanRepository()

    @Provides
    @Singleton
    fun provideInboxRepository(): InboxRepository = FakeInboxRepository()

    @Provides
    @Singleton
    fun provideMerchantRuleRepository(): MerchantRuleRepository = FakeMerchantRuleRepository()

    @Provides
    @Singleton
    fun provideMonitoredAppRepository(): MonitoredAppRepository = FakeMonitoredAppRepository()

    @Provides
    @Singleton
    fun provideBudgetRepository(): BudgetRepository = FakeBudgetRepository()

    @Provides
    @Singleton
    fun provideBudgetAlertLogRepository(): BudgetAlertLogRepository = FakeBudgetAlertLogRepository()

    @Provides
    @Singleton
    fun provideLoanRepository(): LoanRepository = FakeLoanRepository()

    @Provides
    @Singleton
    fun provideValuationRepository(): ValuationRepository = FakeValuationRepository()

    @Provides
    @Singleton
    fun provideAtomicRunner(): AtomicRunner = PassthroughAtomicRunner()
}
