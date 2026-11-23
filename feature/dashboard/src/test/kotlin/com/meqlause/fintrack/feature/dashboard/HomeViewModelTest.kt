package com.meqlause.fintrack.feature.dashboard

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Instant

// FR-040..043, S-03: HomeViewModel against the fake repositories; the clock is pinned to 15 Nov 2026 in Jakarta
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val now = Instant.parse("2026-11-15T03:00:00Z")

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    // FR-040: with no transactions the state is Success with an empty recent list and the wallet total
    @Test
    fun givenNoTransactionsWhenOpenedThenRecentIsEmptyAndTotalIsWalletSum() =
        runTest {
            val transactions = FakeTransactionRepository()
            val wallets = FakeWalletRepository(transactions)
            wallets.insert(wallet("BCA", 1_000_000L))
            val viewModel = viewModel(wallets, transactions)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            val state = viewModel.state.value as HomeUiState.Success
            assertThat(state.total).isEqualTo(Money(1_000_000L))
            assertThat(state.recent.isEmpty()).isEqualTo(true)
            collector.cancel()
        }

    // FR-041: income and expense of the current period; transfers are left out of both
    @Test
    fun givenIncomeAndExpenseThisPeriodWhenOpenedThenPeriodTotalsMatch() =
        runTest {
            val transactions = FakeTransactionRepository()
            val wallets = FakeWalletRepository(transactions)
            val walletId = wallets.insert(wallet("BCA", 0L))
            transactions.insert(movement(walletId, TransactionType.INCOME, 500_000L, now))
            transactions.insert(movement(walletId, TransactionType.EXPENSE, 120_000L, now))
            val viewModel = viewModel(wallets, transactions)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            val period = (viewModel.state.value as HomeUiState.Success).period
            assertThat(period.income).isEqualTo(Money(500_000L))
            assertThat(period.expense).isEqualTo(Money(120_000L))
            assertThat(period.net).isEqualTo(Money(380_000L))
            collector.cancel()
        }

    // FR-042: expenses without a category form a group with no name
    @Test
    fun givenExpenseWithoutCategoryWhenOpenedThenCategoryNameIsNull() =
        runTest {
            val transactions = FakeTransactionRepository()
            val wallets = FakeWalletRepository(transactions)
            val walletId = wallets.insert(wallet("Tunai", 0L))
            transactions.insert(movement(walletId, TransactionType.EXPENSE, 50_000L, now))
            val viewModel = viewModel(wallets, transactions)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            val categories = (viewModel.state.value as HomeUiState.Success).categories
            assertThat(categories.single().name).isNull()
            collector.cancel()
        }

    // FR-040: the privacy toggle flips the shown state for this session
    @Test
    fun givenToggleWhenTappedTwiceThenAmountsAreVisibleAgain() =
        runTest {
            val viewModel = viewModel(FakeWalletRepository(), FakeTransactionRepository())
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            viewModel.toggleHidden()
            advanceUntilIdle()
            assertThat((viewModel.state.value as HomeUiState.Success).hidden).isEqualTo(true)

            viewModel.toggleHidden()
            advanceUntilIdle()
            assertThat((viewModel.state.value as HomeUiState.Success).hidden).isEqualTo(false)
            collector.cancel()
        }

    // FR-040: the summary pill opens Ringkasan and closing removes it
    @Test
    fun givenSummaryOpenedWhenClosedThenNoDrawerIsShown() =
        runTest {
            val viewModel = viewModel(FakeWalletRepository(), FakeTransactionRepository())
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            viewModel.openSummary()
            advanceUntilIdle()
            assertThat((viewModel.state.value as HomeUiState.Success).drawer).isEqualTo(HomeDrawer.SUMMARY)

            viewModel.closeDrawer()
            advanceUntilIdle()
            assertThat((viewModel.state.value as HomeUiState.Success).drawer).isNull()
            collector.cancel()
        }

    // S-03: before anyone collects, the state is Loading
    @Test
    fun givenNoSubscriberWhenReadThenStateIsLoading() {
        val viewModel = viewModel(FakeWalletRepository(), FakeTransactionRepository())
        assertThat(viewModel.state.value).isInstanceOf(HomeUiState.Loading::class)
    }

    // FR-137: a credit wallet appears on the Tagihan card and its chip opens the credit detail
    @Test
    fun givenCreditWalletWhenOpenedThenBillsListItAndChipIsCredit() =
        runTest {
            val transactions = FakeTransactionRepository()
            val wallets = FakeWalletRepository(transactions)
            wallets.insert(creditWallet("BCA Kredit"))
            val viewModel = viewModel(wallets, transactions)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()
            val state = viewModel.state.value as HomeUiState.Success
            assertThat(state.bills.bills.map { it.name }).isEqualTo(listOf("BCA Kredit"))
            assertThat(state.wallets.single().isCredit).isEqualTo(true)
            collector.cancel()
        }

    private fun creditWallet(name: String): Wallet {
        val audit = Audit(createdAt = now, updatedAt = now)
        return Wallet(
            walletId = WalletId(0L),
            kind = WalletKind(type = WalletType.CREDIT_CARD, investKind = null),
            terms = WalletTerms(initialBalance = Money(0L), credit = CreditTerms(Money(5_000_000L), 25, 10)),
            profile = WalletProfile(name = name, appearance = Appearance("card", "blue"), sortOrder = 0),
            status = WalletStatus(),
            audit = audit,
        )
    }

    private fun viewModel(
        wallets: FakeWalletRepository,
        transactions: FakeTransactionRepository,
    ): HomeViewModel =
        HomeViewModel(
            wallets = wallets,
            transactions = transactions,
            categories = FakeCategoryRepository(),
            settings = FakeSettingsRepository(),
            quickEntries = FakeQuickEntryRepository(),
            bills =
                ObserveBillsCardUseCase(
                    wallets,
                    transactions,
                    FakeInstallmentPlanRepository(),
                    FakeSettingsRepository(),
                    FakeClock(now),
                ),
            netWorth =
                ObserveNetWorthUseCase(
                    wallets = wallets,
                    transactions = transactions,
                    plans = FakeInstallmentPlanRepository(),
                    loans = ObserveLoansUseCase(FakeLoanRepository(), transactions),
                    investments = ObserveInvestmentPortfolioUseCase(wallets, transactions, FakeValuationRepository()),
                    clock = FakeClock(now),
                ),
            clock = FakeClock(now),
        )

    private fun wallet(
        name: String,
        initial: Long,
    ): Wallet {
        val audit = Audit(createdAt = now, updatedAt = now)
        return Wallet(
            walletId = WalletId(0L),
            kind = WalletKind(type = WalletType.BANK, investKind = null),
            terms = WalletTerms(initialBalance = Money(initial), credit = null),
            profile = WalletProfile(name = name, appearance = Appearance("bank", "blue"), sortOrder = 0),
            status = WalletStatus(),
            audit = audit,
        )
    }

    private fun movement(
        walletId: WalletId,
        type: TransactionType,
        rupiah: Long,
        at: Instant,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(0L),
            movement = Movement(type = type, amount = Money(rupiah)),
            routing = Routing(walletId = walletId, targetWalletId = null, categoryId = null, loanId = null),
            memo = Memo(),
            origin = Origin(source = TransactionSource.MANUAL, inboxItemId = null),
            timing = Timing(occurredAt = at, audit = Audit(createdAt = at, updatedAt = at)),
        )
}
