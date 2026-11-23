package com.meqlause.fintrack.feature.investment.detail

import androidx.lifecycle.SavedStateHandle
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.usecase.CreateInvestmentUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveInvestmentDetailUseCase
import com.meqlause.fintrack.core.domain.usecase.SaveValuationUseCase
import com.meqlause.fintrack.core.domain.validation.WalletNameValidator
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeValuationRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.repository.PassthroughAtomicRunner
import com.meqlause.fintrack.core.testing.time.FakeClock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant

// FR-172, FR-173, S-20: InvestmentDetailViewModel against the in-memory fakes
@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class InvestmentDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = FakeClock(Instant.parse("2026-11-23T02:00:00Z"))
    private val transactions = FakeTransactionRepository()
    private val wallets = FakeWalletRepository(transactions)
    private val valuations = FakeValuationRepository()

    private suspend fun createFund(): Long =
        requireNotNull(
            CreateInvestmentUseCase(wallets, WalletNameValidator())
                .invoke("Reksa Dana", InvestKind.MUTUAL_FUND, Money(1_000_000L), clock.instant()),
        ).value

    private fun TestScope.opened(walletKey: Long): InvestmentDetailViewModel {
        val model =
            InvestmentDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("walletId" to walletKey)),
                observeDetail = ObserveInvestmentDetailUseCase(wallets, transactions, valuations),
                saveValuation = SaveValuationUseCase(wallets, valuations, PassthroughAtomicRunner()),
                clock = clock,
            )
        backgroundScope.launch { model.state.collect {} }
        return model
    }

    /** S-20: an unknown wallet id ends in a loaded state without detail. */
    @Test
    fun `given an unknown wallet when the screen opens then it is loaded without detail (S-20)`() =
        runTest {
            val model = opened(999L)
            runCurrent()
            assertThat(model.state.value.detail).isNull()
            assertThat(model.state.value.loaded).isEqualTo(true)
        }

    /** FR-172: the valuation keypad starts from the current value, takes digits and saves a new value. */
    @Test
    fun `given an investment when the value is updated then it is stored and the sheet closes (FR-172)`() =
        runTest {
            val model = opened(createFund())
            runCurrent()
            assertThat(model.state.value.detail).isNotNull()
            model.openValuation()
            runCurrent()
            assertThat(model.state.value.valuation).isEqualTo("1000000")
            model.backspace()
            model.typeDigit("5")
            runCurrent()
            assertThat(model.state.value.valuation).isEqualTo("1000005")
            model.save()
            runCurrent()
            assertThat(model.state.value.valuation).isNull()
        }

    /** FR-172: keys and save do nothing without an open keypad; dismiss closes it. */
    @Test
    fun `given no keypad when keys are pressed then nothing happens and dismiss closes the keypad (FR-172)`() =
        runTest {
            val model = opened(createFund())
            model.typeDigit("5")
            model.backspace()
            model.save()
            runCurrent()
            assertThat(model.state.value.valuation).isNull()
            model.openValuation()
            model.dismissValuation()
            runCurrent()
            assertThat(model.state.value.valuation).isNull()
        }

    /** FR-172: the keypad of a missing wallet opens at zero and a rejected save keeps it open. */
    @Test
    fun `given a missing wallet when a value is saved then the keypad stays open (FR-172)`() =
        runTest {
            val model = opened(999L)
            runCurrent()
            model.openValuation()
            model.save()
            runCurrent()
            assertThat(model.state.value.valuation).isEqualTo("0")
        }
}
