package com.meqlause.fintrack.core.data.support

import com.meqlause.fintrack.core.data.repository.LocalBudgetRepository
import com.meqlause.fintrack.core.data.repository.LocalCategoryRepository
import com.meqlause.fintrack.core.data.repository.LocalInboxRepository
import com.meqlause.fintrack.core.data.repository.LocalInstallmentPlanRepository
import com.meqlause.fintrack.core.data.repository.LocalLoanRepository
import com.meqlause.fintrack.core.data.repository.LocalMerchantRuleRepository
import com.meqlause.fintrack.core.data.repository.LocalMonitoredAppRepository
import com.meqlause.fintrack.core.data.repository.LocalQuickEntryRepository
import com.meqlause.fintrack.core.data.repository.LocalSettingsRepository
import com.meqlause.fintrack.core.data.repository.LocalTransactionRepository
import com.meqlause.fintrack.core.data.repository.LocalValuationRepository
import com.meqlause.fintrack.core.data.repository.LocalWalletRepository
import com.meqlause.fintrack.core.database.FintrackDatabase
import com.meqlause.fintrack.core.datastore.SettingsDataSource
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
import com.meqlause.fintrack.core.testing.contract.RepositoryFixture

/** ADR-007, DOC-06: the Local repositories wired to one in-memory database, so foreign keys are enforced. */
internal class LocalRepositoryFixture(
    private val database: FintrackDatabase,
) : RepositoryFixture {
    override val wallets: WalletRepository = LocalWalletRepository(database.walletDao())
    override val categories: CategoryRepository = LocalCategoryRepository(database.categoryDao())
    override val transactions: TransactionRepository = LocalTransactionRepository(database.transactionDao())
    override val budgets: BudgetRepository = LocalBudgetRepository(database.budgetDao())
    override val inbox: InboxRepository = LocalInboxRepository(database.inboxItemDao())
    override val merchantRules: MerchantRuleRepository = LocalMerchantRuleRepository(database.merchantRuleDao())
    override val monitoredApps: MonitoredAppRepository = LocalMonitoredAppRepository(database.monitoredAppDao())
    override val installmentPlans: InstallmentPlanRepository =
        LocalInstallmentPlanRepository(database.installmentPlanDao())
    override val loans: LoanRepository = LocalLoanRepository(database.loanDao())
    override val valuations: ValuationRepository = LocalValuationRepository(database.valuationEntryDao())
    override val quickEntries: QuickEntryRepository = LocalQuickEntryRepository(database.quickEntryDao())
    override val settings: SettingsRepository =
        LocalSettingsRepository(SettingsDataSource(InMemoryPreferencesStore()))

    override fun close() {
        database.close()
    }
}
