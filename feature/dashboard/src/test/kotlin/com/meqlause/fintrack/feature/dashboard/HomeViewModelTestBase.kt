package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.domain.usecase.ObserveBillsCardUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveInvestmentPortfolioUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveLoansUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveNetWorthUseCase
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
import com.meqlause.fintrack.core.testing.repository.FakeInboxRepository
import com.meqlause.fintrack.core.testing.repository.FakeInstallmentPlanRepository
import com.meqlause.fintrack.core.testing.repository.FakeLoanRepository
import com.meqlause.fintrack.core.testing.repository.FakeQuickEntryRepository
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeValuationRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.time.FakeClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import java.time.Instant

// FR-040..043, S-03: shared fixtures for the Beranda view model tests; the clock is pinned to 15 Nov 2026 in Jakarta
@OptIn(ExperimentalCoroutinesApi::class)
abstract class HomeViewModelTestBase {
    protected val dispatcher = StandardTestDispatcher()
    protected val fixedNow: Instant = Instant.parse("2026-11-15T03:00:00Z")

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    protected fun creditWallet(name: String): Wallet {
        val audit = Audit(createdAt = fixedNow, updatedAt = fixedNow)
        return Wallet(
            walletId = WalletId(0L),
            kind = WalletKind(type = WalletType.CREDIT_CARD, investKind = null),
            terms = WalletTerms(initialBalance = Money(0L), credit = CreditTerms(Money(5_000_000L), 25, 10)),
            profile = WalletProfile(name = name, appearance = Appearance("card", "blue"), sortOrder = 0),
            status = WalletStatus(),
            audit = audit,
        )
    }

    // the fakes behind the view model; a test seeds them before it builds the view model
    protected val settings = FakeSettingsRepository()
    protected val quickEntries = FakeQuickEntryRepository()
    protected val inboxItems = FakeInboxRepository()

    internal fun viewModel(
        wallets: FakeWalletRepository,
        transactions: FakeTransactionRepository,
    ): HomeViewModel =
        HomeViewModel(
            repositories =
                HomeRepositories(
                    wallets = wallets,
                    transactions = transactions,
                    categories = FakeCategoryRepository(),
                    settings = settings,
                    inboxItems = inboxItems,
                    quickEntries = quickEntries,
                ),
            bills =
                ObserveBillsCardUseCase(
                    wallets,
                    transactions,
                    FakeInstallmentPlanRepository(),
                    FakeSettingsRepository(),
                    FakeClock(fixedNow),
                ),
            netWorth =
                ObserveNetWorthUseCase(
                    wallets = wallets,
                    transactions = transactions,
                    plans = FakeInstallmentPlanRepository(),
                    loans = ObserveLoansUseCase(FakeLoanRepository(), transactions),
                    investments = ObserveInvestmentPortfolioUseCase(wallets, transactions, FakeValuationRepository()),
                    clock = FakeClock(fixedNow),
                ),
            clock = FakeClock(fixedNow),
        )

    protected fun wallet(
        name: String,
        initial: Long,
    ): Wallet {
        val audit = Audit(createdAt = fixedNow, updatedAt = fixedNow)
        return Wallet(
            walletId = WalletId(0L),
            kind = WalletKind(type = WalletType.BANK, investKind = null),
            terms = WalletTerms(initialBalance = Money(initial), credit = null),
            profile = WalletProfile(name = name, appearance = Appearance("bank", "blue"), sortOrder = 0),
            status = WalletStatus(),
            audit = audit,
        )
    }

    protected fun movement(
        walletId: WalletId,
        type: TransactionType,
        rupiah: Long,
        occurredAt: Instant,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(0L),
            movement = Movement(type = type, amount = Money(rupiah)),
            routing = Routing(walletId = walletId, targetWalletId = null, categoryId = null, loanId = null),
            memo = Memo(),
            origin = Origin(source = TransactionSource.MANUAL, inboxItemId = null),
            timing = Timing(occurredAt = occurredAt, audit = Audit(createdAt = occurredAt, updatedAt = occurredAt)),
        )
}
