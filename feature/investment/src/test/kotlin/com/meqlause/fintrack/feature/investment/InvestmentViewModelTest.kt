package com.meqlause.fintrack.feature.investment

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.usecase.CreateInvestmentUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveInvestmentPortfolioUseCase
import com.meqlause.fintrack.core.domain.validation.WalletNameValidator
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

// FR-170, FR-173, S-08: InvestmentViewModel against the in-memory fakes
@OptIn(ExperimentalCoroutinesApi::class)
class InvestmentViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val clock = FakeClock(Instant.parse("2026-11-23T02:00:00Z"))

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    private fun viewModel(): InvestmentViewModel {
        val transactions = FakeTransactionRepository()
        val wallets = FakeWalletRepository(transactions)
        val valuations = FakeValuationRepository()
        return InvestmentViewModel(
            observePortfolio = ObserveInvestmentPortfolioUseCase(wallets, transactions, valuations),
            createInvestment = CreateInvestmentUseCase(wallets, WalletNameValidator()),
            clock = clock,
        )
    }

    // FR-173: with no investment wallet stored, the portfolio has no accounts and no sheet is open
    @Test
    fun givenNoInvestmentsWhenScreenOpensThenPortfolioHasNoAccounts() =
        runTest {
            val viewModel = viewModel()
            backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()
            assertThat(
                viewModel.state.value.portfolio
                    ?.accounts
                    .orEmpty(),
            ).isEmpty()
            assertThat(viewModel.state.value.draft).isNull()
        }

    // FR-170: "Tambah investasi" opens an empty sheet, and typed digits build the amount
    @Test
    fun givenCreateSheetWhenDigitsTypedThenAmountIsBuilt() =
        runTest {
            val viewModel = viewModel()
            backgroundScope.launch { viewModel.state.collect {} }
            viewModel.startCreate()
            viewModel.typeDigit("5")
            viewModel.typeDigit("0")
            advanceUntilIdle()
            assertThat(
                viewModel.state.value.draft
                    ?.invested,
            ).isEqualTo("50")
        }
}
