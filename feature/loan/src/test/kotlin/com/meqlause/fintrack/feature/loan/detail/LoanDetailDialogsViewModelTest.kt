package com.meqlause.fintrack.feature.loan.detail

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.model.LoanStatus
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
class LoanDetailDialogsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val harness = LoanDetailHarness()

    private suspend fun LoanDetailViewModel.loaded(): LoanDetailUiState = state.first { it.isLoaded }

    @Test
    fun `given the due date sheet when dismissed then no sheet stays open (FR-158)`() =
        runBlocking {
            val model = harness.viewModel(harness.insertLoan())
            model.loaded()
            model.onEvent(LoanDetailEvent.OpenDueDate)

            model.onEvent(LoanDetailEvent.DismissSheet)

            assertThat(model.state.value.overlay.sheet).isNull()
        }

    @Test
    fun `given a write-off dialog when confirmed then the loan is written off (FR-155)`() =
        runBlocking {
            val loanKey = harness.insertLoan()
            val model = harness.viewModel(loanKey)
            model.loaded()
            model.onEvent(LoanDetailEvent.DialogOpened(LoanDialog.WRITE_OFF))
            assertThat(model.state.value.overlay.dialog).isEqualTo(LoanDialog.WRITE_OFF)

            model.onEvent(LoanDetailEvent.DialogConfirmed)

            assertThat(harness.loanOf(loanKey)?.status).isEqualTo(LoanStatus.WRITTEN_OFF)
            assertThat(model.state.value.overlay.dialog).isNull()
            assertThat(model.state.value.finished).isFalse()
        }

    @Test
    fun `given a delete dialog when confirmed then the loan is removed and the screen finishes (BR-046)`() =
        runBlocking {
            val loanKey = harness.insertLoan()
            val model = harness.viewModel(loanKey)
            model.loaded()
            model.onEvent(LoanDetailEvent.DialogOpened(LoanDialog.DELETE))

            model.onEvent(LoanDetailEvent.DialogConfirmed)

            assertThat(harness.loanOf(loanKey)).isNull()
            assertThat(model.state.value.finished).isTrue()
        }

    @Test
    fun `given a dialog when dismissed then it closes and nothing changes (FR-155)`() =
        runBlocking {
            val loanKey = harness.insertLoan()
            val model = harness.viewModel(loanKey)
            model.loaded()
            model.onEvent(LoanDetailEvent.DialogOpened(LoanDialog.DELETE))

            model.onEvent(LoanDetailEvent.DialogDismissed)
            model.onEvent(LoanDetailEvent.DialogConfirmed)

            assertThat(model.state.value.overlay.dialog).isNull()
            assertThat(harness.loanOf(loanKey)?.status).isEqualTo(LoanStatus.OPEN)
        }

    @Test
    fun `given a repayment already stored when repaying again then its wallet is preselected (FR-152)`() =
        runBlocking {
            val loanKey = harness.insertLoan()
            val model = harness.viewModel(loanKey)
            model.loaded()
            model.onEvent(LoanDetailEvent.OpenRepayment)
            "100000".forEach { model.onEvent(LoanDetailEvent.DigitTyped(it.toString())) }
            model.onEvent(LoanDetailEvent.WalletPicked(WalletId(3L)))
            model.onEvent(LoanDetailEvent.SaveRepayment)

            model.onEvent(LoanDetailEvent.OpenRepayment)

            assertThat(model.state.value.repayment.walletId).isEqualTo(WalletId(3L))
            assertThat(model.state.value.repayment.rupiahDigits).isEqualTo("")
        }

    @Test
    fun `given an unknown loan when repaying then the sheet opens without a preselected wallet (FR-152)`() =
        runBlocking {
            val model = harness.viewModel(999L)
            model.loaded()

            model.onEvent(LoanDetailEvent.OpenRepayment)

            assertThat(model.state.value.overlay.sheet).isEqualTo(LoanDetailSheet.REPAYMENT)
            assertThat(model.state.value.repayment.walletId).isNull()
        }

    @Test
    fun `given several loans when one is opened then only its entry is shown (FR-153)`() =
        runBlocking {
            harness.insertLoan()
            val second = harness.insertLoan()

            val state = harness.viewModel(second).loaded()

            assertThat(
                state.entry
                    ?.loan
                    ?.loanId
                    ?.value,
            ).isEqualTo(second)
        }

    @Test
    fun `given no dialog when confirmed then nothing happens (FR-155)`() =
        runBlocking {
            val loanKey = harness.insertLoan()
            val model = harness.viewModel(loanKey)
            model.loaded()

            model.onEvent(LoanDetailEvent.DialogConfirmed)

            assertThat(harness.loanOf(loanKey)?.status).isEqualTo(LoanStatus.OPEN)
            assertThat(model.state.value.finished).isFalse()
        }
}
