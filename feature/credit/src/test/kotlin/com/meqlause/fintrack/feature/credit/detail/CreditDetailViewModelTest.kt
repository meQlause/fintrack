package com.meqlause.fintrack.feature.credit.detail

import androidx.lifecycle.SavedStateHandle
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.usecase.SaveTransactionUseCase
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.StatementStatus
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

// FR-131 to FR-138, S-14, S-15: the credit wallet detail reads the statement and writes bill payments and days
@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class CreditDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val clock = FakeClock(Instant.parse("2026-11-05T03:00:00Z"))
    private val audit = Audit(createdAt = Instant.parse("2026-01-01T00:00:00Z"), updatedAt = Instant.EPOCH)
    private val transactions = FakeTransactionRepository()
    private val wallets = FakeWalletRepository(transactions)
    private val plans = FakeInstallmentPlanRepository()

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun givenCardWithSpendingWhenObservedThenTheBillIsDue() =
        runTest {
            val cardId = wallets.insert(wallet("BCA Visa", WalletType.CREDIT_CARD))
            transactions.insert(row(cardId, TransactionType.EXPENSE, 3_500_000L, null, "2026-10-05T03:00:00Z"))
            val viewModel = viewModel(cardId)

            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            val success = viewModel.state.value as CreditDetailUiState.Success
            assertThat(success.statement.status).isEqualTo(StatementStatus.DUE)
            assertThat(success.statement.breakdown.remainingDue).isEqualTo(Money(3_500_000L))
            collector.cancel()
        }

    @Test
    fun givenBillPaidFromBankWhenSavedThenATransferIsStoredAndTheSheetCloses() =
        runTest {
            val cardId = wallets.insert(wallet("BCA Visa", WalletType.CREDIT_CARD))
            val bankId = wallets.insert(wallet("BCA", WalletType.BANK))
            transactions.insert(row(cardId, TransactionType.EXPENSE, 3_500_000L, null, "2026-10-05T03:00:00Z"))
            val viewModel = viewModel(cardId)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            viewModel.openPay()
            viewModel.setPayAmount("1000000")
            viewModel.pickSource(bankId)
            viewModel.payBill()
            advanceUntilIdle()

            val stored = transactions.observeAll().first().last()
            assertThat(stored.movement.type).isEqualTo(TransactionType.TRANSFER)
            assertThat(stored.routing.walletId).isEqualTo(bankId)
            assertThat(stored.routing.targetWalletId).isEqualTo(cardId)
            assertThat((viewModel.state.value as CreditDetailUiState.Success).pay).isNull()
            collector.cancel()
        }

    @Test
    fun givenNewStatementDayWhenPickedThenItIsStoredOnTheWallet() =
        runTest {
            val cardId = wallets.insert(wallet("BCA Visa", WalletType.CREDIT_CARD))
            val viewModel = viewModel(cardId)
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            viewModel.openPicker(CreditDay.STATEMENT)
            viewModel.pickDay(20)
            advanceUntilIdle()

            val stored = wallets.observeAll().first().single()
            assertThat(stored.terms.credit?.statementDay).isEqualTo(20)
            collector.cancel()
        }

    private fun viewModel(walletId: WalletId): CreditDetailViewModel =
        CreditDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("walletId" to walletId.value)),
            wallets = wallets,
            transactions = transactions,
            plans = plans,
            saveTransaction = SaveTransactionUseCase(transactions, wallets, FakeCategoryRepository(), clock),
            clock = clock,
        )

    private fun wallet(
        name: String,
        type: WalletType,
    ): Wallet =
        Wallet(
            walletId = WalletId(0L),
            kind = WalletKind(type, investKind = null),
            terms =
                WalletTerms(
                    initialBalance = Money(0L),
                    credit = if (type == WalletType.BANK) null else CreditTerms(Money(10_000_000L), 25, 10),
                ),
            profile = WalletProfile(name, Appearance("wallet", "blue"), sortOrder = 0),
            status = WalletStatus(),
            audit = audit,
        )

    private fun row(
        walletId: WalletId,
        type: TransactionType,
        amount: Long,
        target: WalletId?,
        at: String,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(0L),
            movement = Movement(type, Money(amount)),
            routing = Routing(walletId, target, categoryId = null, loanId = null),
            memo = Memo(),
            origin = Origin(TransactionSource.MANUAL, inboxItemId = null),
            timing = Timing(Instant.parse(at), audit),
        )
}
