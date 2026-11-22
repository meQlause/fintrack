package com.meqlause.fintrack.core.testing.contract

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

/**
 * ADR-007, DOC-06: every repository a contract test may touch, all backed by one store so that
 * foreign keys resolve the same way in the database and in the fakes.
 */
public interface RepositoryFixture {
    /** FR-010: wallet repository of this store. */
    public val wallets: WalletRepository

    /** FR-004: category repository of this store. */
    public val categories: CategoryRepository

    /** FR-030: transaction repository of this store. */
    public val transactions: TransactionRepository

    /** FR-074: budget repository of this store. */
    public val budgets: BudgetRepository

    /** FR-090: inbox repository of this store. */
    public val inbox: InboxRepository

    /** FR-088: merchant rule repository of this store. */
    public val merchantRules: MerchantRuleRepository

    /** FR-090: monitored app repository of this store. */
    public val monitoredApps: MonitoredAppRepository

    /** FR-140: instalment plan repository of this store. */
    public val installmentPlans: InstallmentPlanRepository

    /** FR-150: loan repository of this store. */
    public val loans: LoanRepository

    /** FR-172: valuation repository of this store. */
    public val valuations: ValuationRepository

    /** FR-039: quick entry repository of this store. */
    public val quickEntries: QuickEntryRepository

    /** DOC-02: settings repository of this store. */
    public val settings: SettingsRepository

    /** ADR-007: releases the store; a no-op for in-memory fakes. */
    public fun close()
}
