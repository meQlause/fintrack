package com.meqlause.fintrack.feature.loan.detail

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.loan.LoanDueOption
import com.meqlause.fintrack.core.domain.loan.RepaymentError
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// FR-152, FR-155, FR-158, S-17: the loan detail ViewModel handles every event and reports every outcome
@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class LoanDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val harness = LoanDetailHarness()

    private suspend fun LoanDetailViewModel.loaded(): LoanDetailUiState = state.first { it.isLoaded }

    @Test
    fun `given an open loan when opened then it is loaded and can be repaid or written off (FR-152, FR-155)`() =
        runBlocking {
            val loanKey = harness.insertLoan()
            val state = harness.viewModel(loanKey).loaded()

            assertThat(
                state.entry
                    ?.loan
                    ?.loanId
                    ?.value,
            ).isEqualTo(loanKey)
            assertThat(state.canRepay).isTrue()
            assertThat(state.canWriteOff).isTrue()
        }

    @Test
    fun `given an unknown loan when opened then no entry is shown and nothing can be done (FR-153)`() =
        runBlocking {
            val state = harness.viewModel(999L).loaded()

            assertThat(state.entry).isNull()
            assertThat(state.canRepay).isFalse()
            assertThat(state.canWriteOff).isFalse()
        }

    @Test
    fun `given the keypad when digits are typed and erased then the amount follows (FR-152)`() =
        runBlocking {
            val model = harness.viewModel(harness.insertLoan())
            model.loaded()

            model.onEvent(LoanDetailEvent.OpenRepayment)
            model.onEvent(LoanDetailEvent.DigitTyped("0"))
            model.onEvent(LoanDetailEvent.DigitTyped("1"))
            model.onEvent(LoanDetailEvent.DigitTyped("5"))
            model.onEvent(LoanDetailEvent.BackspacePressed)

            val state = model.state.value
            assertThat(state.overlay.sheet).isEqualTo(LoanDetailSheet.REPAYMENT)
            assertThat(state.repayment.rupiahDigits).isEqualTo("1")
        }

    @Test
    fun `given sixteen digits when typed then the amount keeps at most fifteen (FR-152)`() =
        runBlocking {
            val model = harness.viewModel(harness.insertLoan())
            model.loaded()

            repeat(16) { model.onEvent(LoanDetailEvent.DigitTyped("9")) }

            assertThat(model.state.value.repayment.rupiahDigits.length).isEqualTo(15)
        }

    @Test
    fun `given a wallet picked when repaying then the wallet is kept on the form (FR-152)`() =
        runBlocking {
            val model = harness.viewModel(harness.insertLoan())
            model.loaded()

            model.onEvent(LoanDetailEvent.WalletPicked(WalletId(4L)))

            assertThat(model.state.value.repayment.walletId).isEqualTo(WalletId(4L))
        }

    @Test
    fun `given an empty form when saved then the amount and wallet are reported and the sheet stays (FR-152)`() =
        runBlocking {
            val model = harness.viewModel(harness.insertLoan())
            model.loaded()
            model.onEvent(LoanDetailEvent.OpenRepayment)

            model.onEvent(LoanDetailEvent.SaveRepayment)

            val state = model.state.value
            assertThat(
                state.repayment.errors,
            ).containsOnly(RepaymentError.AMOUNT_REQUIRED, RepaymentError.WALLET_REQUIRED)
            assertThat(state.overlay.sheet).isEqualTo(LoanDetailSheet.REPAYMENT)
        }

    @Test
    fun `given an amount above the balance when saved then it is rejected (BR-041)`() =
        runBlocking {
            val model = harness.viewModel(harness.insertLoan())
            model.loaded()
            model.onEvent(LoanDetailEvent.OpenRepayment)
            "999999".forEach { model.onEvent(LoanDetailEvent.DigitTyped(it.toString())) }
            model.onEvent(LoanDetailEvent.WalletPicked(WalletId(1L)))

            model.onEvent(LoanDetailEvent.SaveRepayment)

            assertThat(model.state.value.repayment.errors).containsOnly(RepaymentError.AMOUNT_ABOVE_OUTSTANDING)
            assertThat(harness.transactions.observeAll().first()).isEmpty()
        }

    @Test
    fun `given a valid repayment when saved then it is stored and the sheet closes (FR-152)`() =
        runBlocking {
            val model = harness.viewModel(harness.insertLoan())
            model.loaded()
            model.onEvent(LoanDetailEvent.OpenRepayment)
            "100000".forEach { model.onEvent(LoanDetailEvent.DigitTyped(it.toString())) }
            model.onEvent(LoanDetailEvent.WalletPicked(WalletId(1L)))

            model.onEvent(LoanDetailEvent.SaveRepayment)

            val state = model.state.value
            assertThat(state.overlay.sheet).isNull()
            assertThat(state.repayment.rupiahDigits).isEqualTo("")
            assertThat(harness.transactions.observeAll().first()).hasSize(1)
        }

    @Test
    fun `given the due date sheet when a due date is picked then the loan gets it and the sheet closes (FR-158)`() =
        runBlocking {
            val loanKey = harness.insertLoan()
            val model = harness.viewModel(loanKey)
            model.loaded()

            model.onEvent(LoanDetailEvent.OpenDueDate)
            assertThat(model.state.value.overlay.sheet).isEqualTo(LoanDetailSheet.DUE_DATE)
            model.onEvent(LoanDetailEvent.DuePicked(LoanDueOption.ONE_WEEK))

            assertThat(harness.loanOf(loanKey)?.agreement?.dueDate).isNotNull()
            assertThat(model.state.value.overlay.sheet).isNull()
        }
}
