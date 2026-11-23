package com.meqlause.fintrack.feature.credit.detail

import androidx.lifecycle.SavedStateHandle
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
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
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
import com.meqlause.fintrack.core.testing.repository.FakeInstallmentPlanRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.time.FakeClock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant

// FR-132, FR-133, FR-138, S-15: the pay sheet and day drawer paths of CreditDetailViewModel
@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class CreditDetailPayFlowTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = FakeClock(Instant.parse("2026-11-05T03:00:00Z"))
    private val transactions = FakeTransactionRepository()
    private val wallets = FakeWalletRepository(transactions)

    private suspend fun wallet(
        name: String,
        type: WalletType,
    ): WalletId =
        wallets.insert(
            Wallet(
                walletId = WalletId(0L),
                kind = WalletKind(type, investKind = null),
                terms =
                    WalletTerms(
                        Money(0L),
                        if (type ==
                            WalletType.BANK
                        ) {
                            null
                        } else {
                            CreditTerms(Money(10_000_000L), 25, 10)
                        },
                    ),
                profile = WalletProfile(name, Appearance("wallet", "blue"), sortOrder = 0),
                status = WalletStatus(),
                audit = Audit(Instant.EPOCH, Instant.EPOCH),
            ),
        )

    private suspend fun spend(card: WalletId) {
        transactions.insert(
            Transaction(
                transactionId = TransactionId(0L),
                movement = Movement(TransactionType.EXPENSE, Money(2_000_000L)),
                routing = Routing(card, null, categoryId = null, loanId = null),
                memo = Memo(),
                origin = Origin(TransactionSource.MANUAL, inboxItemId = null),
                timing = Timing(Instant.parse("2026-10-05T03:00:00Z"), Audit(Instant.EPOCH, Instant.EPOCH)),
            ),
        )
    }

    private fun TestScope.opened(card: WalletId): CreditDetailViewModel {
        val model =
            CreditDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("walletId" to card.value)),
                wallets = wallets,
                transactions = transactions,
                plans = FakeInstallmentPlanRepository(),
                saveTransaction = SaveTransactionUseCase(transactions, wallets, FakeCategoryRepository(), clock),
                clock = clock,
            )
        backgroundScope.launch { model.state.collect {} }
        return model
    }

    private fun CreditDetailViewModel.success(): CreditDetailUiState.Success =
        state.value as CreditDetailUiState.Success

    /** FR-132: the bill without a source or an amount is refused and marked failed; dismiss closes the sheet. */
    @Test
    fun `given the pay sheet when saved without source or amount then it fails and dismiss closes it (FR-132)`() =
        runTest {
            val model = opened(wallet("BCA Visa", WalletType.CREDIT_CARD))
            advanceUntilIdle()
            model.payBill()
            model.openPay()
            model.payBill()
            advanceUntilIdle()
            assertThat(model.success().payDraft?.failed).isEqualTo(true)
            model.dismissPay()
            advanceUntilIdle()
            assertThat(model.success().payDraft).isNull()
        }

    /** FR-133: Lunas fills the remaining due, the amount keeps digits only, Sebagian clears it. */
    @Test
    fun `given a bill when the amount is filled typed and cleared then digits only are kept (FR-133)`() =
        runTest {
            val card = wallet("BCA Visa", WalletType.CREDIT_CARD)
            spend(card)
            val model = opened(card)
            advanceUntilIdle()
            model.openPay()
            model.fillRemaining()
            advanceUntilIdle()
            assertThat(model.success().payDraft?.typedDigits).isEqualTo("2000000")
            model.setPayAmount("12ab3")
            advanceUntilIdle()
            assertThat(model.success().payDraft?.typedDigits).isEqualTo("123")
            model.clearAmount()
            advanceUntilIdle()
            assertThat(model.success().payDraft?.typedDigits).isEqualTo("")
        }

    /** BR-039: the default source is the first non-credit wallet and the bill is stored as one transfer. */
    @Test
    fun `given a bank wallet when the bill is paid without picking a source then the first one is used (BR-039)`() =
        runTest {
            val card = wallet("BCA Visa", WalletType.CREDIT_CARD)
            val bank = wallet("BCA", WalletType.BANK)
            spend(card)
            val model = opened(card)
            advanceUntilIdle()
            assertThat(model.success().sources.map { it.walletId }).isEqualTo(listOf(bank))
            model.openPay()
            model.setPayAmount("500000")
            model.payBill()
            advanceUntilIdle()
            val stored = transactions.observeAll().first().last()
            assertThat(stored.routing.walletId).isEqualTo(bank)
            assertThat(model.success().payDraft).isNull()
        }

    /** FR-132: a payment the rules reject keeps the sheet open and marked failed. */
    @Test
    fun `given an amount above the limit when saved then the sheet stays open and failed (FR-132)`() =
        runTest {
            val card = wallet("BCA Visa", WalletType.CREDIT_CARD)
            wallet("BCA", WalletType.BANK)
            val model = opened(card)
            advanceUntilIdle()
            model.openPay()
            model.setPayAmount("0")
            model.payBill()
            advanceUntilIdle()
            assertThat(model.success().payDraft?.failed).isEqualTo(true)
            model.pickSource(card)
            model.payBill()
            advanceUntilIdle()
            assertThat(model.success().payDraft?.failed).isEqualTo(true)
        }

    /** FR-138: the day drawer opens, can be dismissed, stores a due day and ignores a pick without a drawer. */
    @Test
    fun `given the day drawer when a due day is picked then it is stored (FR-138)`() =
        runTest {
            val card = wallet("BCA Visa", WalletType.CREDIT_CARD)
            val model = opened(card)
            advanceUntilIdle()
            model.pickDay(3)
            model.openPicker(CreditDay.DUE)
            model.dismissPicker()
            model.pickDay(3)
            advanceUntilIdle()
            assertThat(dueDay()).isEqualTo(10)
            model.openPicker(CreditDay.DUE)
            model.pickDay(3)
            advanceUntilIdle()
            assertThat(dueDay()).isEqualTo(3)
            assertThat(model.success().picker).isNull()
        }

    /** FR-133: the amount keys do nothing while no pay sheet is open, also on a missing wallet. */
    @Test
    fun `given no pay sheet when amount keys are pressed then nothing changes (FR-133)`() =
        runTest {
            val model = opened(wallet("BCA Visa", WalletType.CREDIT_CARD))
            advanceUntilIdle()
            model.setPayAmount("5")
            model.pickSource(WalletId(1L))
            model.fillRemaining()
            model.clearAmount()
            advanceUntilIdle()
            assertThat(model.success().payDraft).isNull()
            val missing = opened(WalletId(99L))
            advanceUntilIdle()
            missing.openPay()
            missing.fillRemaining()
            missing.payBill()
            advanceUntilIdle()
            assertThat(missing.state.value).isInstanceOf(CreditDetailUiState.Missing::class)
        }

    /** FR-131: an unknown wallet id ends in the missing state. */
    @Test
    fun `given an unknown wallet when the detail opens then it is missing (FR-131)`() =
        runTest {
            val model = opened(WalletId(99L))
            advanceUntilIdle()
            assertThat(model.state.value).isInstanceOf(CreditDetailUiState.Missing::class)
        }

    private suspend fun dueDay(): Int? =
        wallets
            .observeAll()
            .first()
            .single()
            .terms.credit
            ?.dueDay
}
