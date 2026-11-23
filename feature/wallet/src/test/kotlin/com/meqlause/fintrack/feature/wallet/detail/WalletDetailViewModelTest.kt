package com.meqlause.fintrack.feature.wallet.detail

import androidx.lifecycle.SavedStateHandle
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
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
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant

// FR-017, S-19: WalletDetailViewModel against the fake repositories
@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class WalletDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val clock = FakeClock(Instant.parse("2026-11-23T00:00:00Z"))
    private val audit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH)

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    private fun viewModel(
        transactions: FakeTransactionRepository,
        wallets: FakeWalletRepository,
        walletId: Long,
    ): WalletDetailViewModel =
        WalletDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("walletId" to walletId)),
            wallets = wallets,
            transactions = transactions,
            categories = FakeCategoryRepository(),
            clock = clock,
        )

    private fun wallet(name: String): Wallet =
        Wallet(
            walletId = WalletId(0L),
            kind = WalletKind(type = WalletType.BANK, investKind = null),
            terms = WalletTerms(initialBalance = Money(100_000L), credit = null),
            profile =
                WalletProfile(
                    name = name,
                    appearance = Appearance(iconKey = "wallet", colorKey = "gray"),
                    sortOrder = 0,
                ),
            status = WalletStatus(),
            audit = audit,
        )

    private fun expense(
        walletId: WalletId,
        dayOfMonth: Int,
        rupiah: Long,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(0L),
            movement = Movement(type = TransactionType.EXPENSE, amount = Money(rupiah)),
            routing = Routing(walletId = walletId, targetWalletId = null, categoryId = null, loanId = null),
            memo = Memo(note = "", merchant = "Toko $dayOfMonth"),
            origin = Origin(source = TransactionSource.MANUAL, inboxItemId = null),
            timing = Timing(occurredAt = Instant.parse("2026-11-%02dT08:00:00Z".format(dayOfMonth)), audit = audit),
        )

    // FR-017: a wallet id that is not stored shows the Missing state
    @Test
    fun givenUnknownWalletWhenOpenedThenStateIsMissing() =
        runTest {
            val transactions = FakeTransactionRepository()
            val viewModel = viewModel(transactions, FakeWalletRepository(transactions), walletId = 9L)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            assertThat(viewModel.state.value).isInstanceOf(WalletDetailUiState.Missing::class)
            collector.cancel()
        }

    // FR-017: only this wallet's transactions appear, newest first, and the list starts collapsed
    @Test
    fun givenTransactionsOfTwoWalletsWhenOpenedThenOnlyThisWalletsRowsShownNewestFirst() =
        runTest {
            val transactions = FakeTransactionRepository()
            val wallets = FakeWalletRepository(transactions)
            val walletId = wallets.insert(wallet("BCA"))
            val otherId = wallets.insert(wallet("Tunai"))
            transactions.insert(expense(walletId, dayOfMonth = 1, rupiah = 10_000L))
            transactions.insert(expense(otherId, dayOfMonth = 2, rupiah = 20_000L))
            transactions.insert(expense(walletId, dayOfMonth = 3, rupiah = 30_000L))
            val viewModel = viewModel(transactions, wallets, walletId.value)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            val state = viewModel.state.value as WalletDetailUiState.Success
            assertThat(state.rows).hasSize(2)
            assertThat(state.rows.first().amount).isEqualTo(Money(30_000L))
            assertThat(state.expanded).isEqualTo(false)
            collector.cancel()
        }

    // FR-017: "Lihat semua" toggles the expanded flag that the screen reads
    @Test
    fun givenExpandedToggledWhenCalledTwiceThenStateIsCollapsedAgain() =
        runTest {
            val transactions = FakeTransactionRepository()
            val wallets = FakeWalletRepository(transactions)
            val walletId = wallets.insert(wallet("BCA"))
            val viewModel = viewModel(transactions, wallets, walletId.value)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            viewModel.toggleExpanded()
            advanceUntilIdle()
            viewModel.toggleExpanded()
            advanceUntilIdle()

            val state = viewModel.state.value as WalletDetailUiState.Success
            assertThat(state.expanded).isEqualTo(false)
            assertThat(wallets.observeAll().first()).hasSize(1)
            collector.cancel()
        }
}
