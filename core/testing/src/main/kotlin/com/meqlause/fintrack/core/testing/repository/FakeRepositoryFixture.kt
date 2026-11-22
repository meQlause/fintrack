package com.meqlause.fintrack.core.testing.repository

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

/**
 * ADR-007, DOC-06: a complete set of in-memory fakes. The wallet fake reads the transaction fake's rows,
 * so balances are computed from the same transactions the test inserted.
 */
public class FakeRepositoryFixture : RepositoryFixture {
    private val transactionStore = FakeTransactionRepository()

    override val wallets: WalletRepository = FakeWalletRepository(transactionStore)
    override val categories: CategoryRepository = FakeCategoryRepository()
    override val transactions: TransactionRepository = transactionStore
    override val budgets: BudgetRepository = FakeBudgetRepository()
    override val inbox: InboxRepository = FakeInboxRepository()
    override val merchantRules: MerchantRuleRepository = FakeMerchantRuleRepository()
    override val monitoredApps: MonitoredAppRepository = FakeMonitoredAppRepository()
    override val installmentPlans: InstallmentPlanRepository = FakeInstallmentPlanRepository()
    override val loans: LoanRepository = FakeLoanRepository()
    override val valuations: ValuationRepository = FakeValuationRepository()
    override val quickEntries: QuickEntryRepository = FakeQuickEntryRepository()
    override val settings: SettingsRepository = FakeSettingsRepository()

    override fun close() = Unit
}
