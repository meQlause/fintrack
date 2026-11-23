package com.meqlause.fintrack.feature.wallet

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.domain.usecase.ObserveInvestmentPortfolioUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveLoansUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveNetWorthUseCase
import com.meqlause.fintrack.core.domain.validation.WalletNameError
import com.meqlause.fintrack.core.domain.validation.WalletNameValidator
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.testing.repository.FakeInstallmentPlanRepository
import com.meqlause.fintrack.core.testing.repository.FakeLoanRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeValuationRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.time.FakeClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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

// FR-002, FR-010..012, S-09: WalletViewModel against the fake repository
@OptIn(ExperimentalCoroutinesApi::class)
class WalletViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    private fun viewModel(repository: FakeWalletRepository): WalletViewModel =
        WalletViewModel(repository, WalletNameValidator(), netWorth(repository), clock)

    private fun netWorth(repository: FakeWalletRepository): ObserveNetWorthUseCase =
        ObserveNetWorthUseCase(
            wallets = repository,
            transactions = FakeTransactionRepository(),
            plans = FakeInstallmentPlanRepository(),
            loans = ObserveLoansUseCase(FakeLoanRepository(), FakeTransactionRepository()),
            investments =
                ObserveInvestmentPortfolioUseCase(repository, FakeTransactionRepository(), FakeValuationRepository()),
            clock = clock,
        )

    private val clock = FakeClock(Instant.parse("2026-01-01T00:00:00Z"))

    // FR-010: with no wallet stored, the state is Empty
    @Test
    fun givenNoWalletsWhenScreenOpensThenStateIsEmpty() =
        runTest {
            val viewModel = viewModel(FakeWalletRepository())
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            assertThat(viewModel.state.value).isInstanceOf(WalletUiState.Empty::class)
            collector.cancel()
        }

    // FR-010: a saved new wallet is stored with its name trimmed and its opening balance
    @Test
    fun givenNewDraftWhenSavedThenWalletIsStoredWithTrimmedName() =
        runTest {
            val repository = FakeWalletRepository()
            val viewModel = viewModel(repository)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            viewModel.startCreate()
            viewModel.edit { it.withName("  Dompet tunai  ") }
            viewModel.edit { it.withInitialBalance("150000") }
            viewModel.save()
            advanceUntilIdle()

            val stored = repository.observeAll().first().single()
            assertThat(stored.profile.name).isEqualTo("Dompet tunai")
            assertThat(stored.terms.initialBalance).isEqualTo(Money(150_000L))
            collector.cancel()
        }

    // FR-002, FR-010: a blank name is rejected; nothing is stored and the editor keeps the reason
    @Test
    fun givenBlankNameWhenSavedThenNothingIsStoredAndEditorShowsReason() =
        runTest {
            val repository = FakeWalletRepository()
            val viewModel = viewModel(repository)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            viewModel.startCreate()
            viewModel.edit { it.withName("   ") }
            viewModel.save()
            advanceUntilIdle()

            assertThat(repository.observeAll().first()).isEqualTo(emptyList())
            val editor = (viewModel.state.value as WalletUiState.Empty).editor
            assertThat(editor?.nameError).isEqualTo(WalletNameError.BLANK)
            collector.cancel()
        }

    // FR-012, FR-014: an archived wallet leaves the total and is listed under the archived heading
    @Test
    fun givenWalletWhenArchivedThenTotalExcludesItAndItIsListedAsArchived() =
        runTest {
            val repository = FakeWalletRepository()
            repository.insert(walletNamed("Dompet tunai", balance = 100_000L))
            val viewModel = viewModel(repository)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            val stored = repository.observeAll().first().single()
            viewModel.startEdit(WalletItem(wallet = stored, balance = Money(100_000L)))
            viewModel.setArchivedEditing(archived = true)
            advanceUntilIdle()

            val success = viewModel.state.value as WalletUiState.Success
            assertThat(success.netWorth.saldo).isEqualTo(Money(0L))
            assertThat(
                success.archived
                    .single()
                    .wallet.profile.name,
            ).isEqualTo("Dompet tunai")
            collector.cancel()
        }

    // BR-053: Saldo leaves out investment wallets, so their balance never reaches the Dompet total
    @Test
    fun givenInvestmentWalletWhenStateBuiltThenSaldoCountsOnlyCash() =
        runTest {
            val repository = FakeWalletRepository()
            repository.insert(walletNamed("Dompet tunai", balance = 100_000L))
            repository.insert(walletNamed("Reksa dana", balance = 900_000L, type = WalletType.INVESTMENT))
            val viewModel = viewModel(repository)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            val success = viewModel.state.value as WalletUiState.Success
            assertThat(success.netWorth.saldo).isEqualTo(Money(100_000L))
            collector.cancel()
        }

    private fun walletNamed(
        name: String,
        balance: Long,
        type: WalletType = WalletType.CASH,
    ): Wallet =
        Wallet(
            walletId = WalletId(0L),
            kind = WalletKind(type = type, investKind = null),
            terms = WalletTerms(initialBalance = Money(balance), credit = null),
            profile =
                WalletProfile(
                    name = name,
                    appearance = Appearance(iconKey = "cash", colorKey = "green"),
                    sortOrder = 0,
                ),
            status = WalletStatus(),
            audit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH),
        )
}
