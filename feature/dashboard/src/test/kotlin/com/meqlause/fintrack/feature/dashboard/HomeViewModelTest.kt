package com.meqlause.fintrack.feature.dashboard

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

// FR-040..043, S-03: HomeViewModel against the fake repositories; the clock is pinned to 15 Nov 2026 in Jakarta
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest : HomeViewModelTestBase() {
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
            transactions.insert(movement(walletId, TransactionType.INCOME, 500_000L, fixedNow))
            transactions.insert(movement(walletId, TransactionType.EXPENSE, 120_000L, fixedNow))
            val viewModel = viewModel(wallets, transactions)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            val period = (viewModel.state.value as HomeUiState.Success).period
            assertThat(period.income).isEqualTo(Money(500_000L))
            assertThat(period.expense).isEqualTo(Money(120_000L))
            assertThat(period.netTotal).isEqualTo(Money(380_000L))
            collector.cancel()
        }

    // FR-042: expenses without a category form a group with no name
    @Test
    fun givenExpenseWithoutCategoryWhenOpenedThenCategoryNameIsNull() =
        runTest {
            val transactions = FakeTransactionRepository()
            val wallets = FakeWalletRepository(transactions)
            val walletId = wallets.insert(wallet("Tunai", 0L))
            transactions.insert(movement(walletId, TransactionType.EXPENSE, 50_000L, fixedNow))
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
}
