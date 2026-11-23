package com.meqlause.fintrack.di

import android.content.Context
import com.meqlause.fintrack.core.database.FintrackDatabase
import com.meqlause.fintrack.core.database.FintrackDatabaseFactory
import com.meqlause.fintrack.core.database.dao.BudgetAlertLogDao
import com.meqlause.fintrack.core.database.dao.BudgetDao
import com.meqlause.fintrack.core.database.dao.CategoryDao
import com.meqlause.fintrack.core.database.dao.InboxItemDao
import com.meqlause.fintrack.core.database.dao.InstallmentPlanDao
import com.meqlause.fintrack.core.database.dao.LoanDao
import com.meqlause.fintrack.core.database.dao.MerchantRuleDao
import com.meqlause.fintrack.core.database.dao.MonitoredAppDao
import com.meqlause.fintrack.core.database.dao.QuickEntryDao
import com.meqlause.fintrack.core.database.dao.TransactionDao
import com.meqlause.fintrack.core.database.dao.ValuationEntryDao
import com.meqlause.fintrack.core.database.dao.WalletDao
import com.meqlause.fintrack.core.security.AndroidKeystoreWrapper
import com.meqlause.fintrack.core.security.DatabaseKeyProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** NFR-003, ADR-009: the encrypted Room database; its passphrase is unwrapped with the Android Keystore key. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): FintrackDatabase {
        val keyProvider = DatabaseKeyProvider(context.filesDir, AndroidKeystoreWrapper())
        return FintrackDatabaseFactory.open(context, keyProvider.passphrase())
    }

    @Provides
    fun provideWalletDao(database: FintrackDatabase): WalletDao = database.walletDao()

    @Provides
    fun provideCategoryDao(database: FintrackDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideTransactionDao(database: FintrackDatabase): TransactionDao = database.transactionDao()

    // FR-140: instalment plans are read and written through Room
    @Provides
    fun provideInstallmentPlanDao(database: FintrackDatabase): InstallmentPlanDao = database.installmentPlanDao()

    // FR-070, FR-073: budget limits and the sent-alert log
    @Provides
    fun provideBudgetDao(database: FintrackDatabase): BudgetDao = database.budgetDao()

    @Provides
    fun provideBudgetAlertLogDao(database: FintrackDatabase): BudgetAlertLogDao = database.budgetAlertLogDao()

    // FR-039: shortcuts are read and written through the Room DAO
    @Provides
    fun provideQuickEntryDao(database: FintrackDatabase): QuickEntryDao = database.quickEntryDao()

    // FR-084: detected notifications, merchant memory and monitored apps are read and written through Room
    @Provides
    fun provideInboxItemDao(database: FintrackDatabase): InboxItemDao = database.inboxItemDao()

    @Provides
    fun provideMerchantRuleDao(database: FintrackDatabase): MerchantRuleDao = database.merchantRuleDao()

    @Provides
    fun provideMonitoredAppDao(database: FintrackDatabase): MonitoredAppDao = database.monitoredAppDao()

    // FR-172: manual valuations of investment wallets
    @Provides
    fun provideValuationEntryDao(database: FintrackDatabase): ValuationEntryDao = database.valuationEntryDao()

    // FR-150: loans and their repayments
    @Provides
    fun provideLoanDao(database: FintrackDatabase): LoanDao = database.loanDao()
}
