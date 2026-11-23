package com.meqlause.fintrack.feature.loan.detail

import androidx.lifecycle.SavedStateHandle
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
import com.meqlause.fintrack.core.domain.usecase.ChangeLoanDueDateUseCase
import com.meqlause.fintrack.core.domain.usecase.DeleteLoanUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveLoansUseCase
import com.meqlause.fintrack.core.domain.usecase.RecordRepaymentUseCase
import com.meqlause.fintrack.core.domain.usecase.WriteOffLoanUseCase
import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeLoanRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.repository.PassthroughAtomicRunner
import com.meqlause.fintrack.core.testing.time.FakeClock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant

// FR-152, FR-155, FR-158, S-17: the loan detail ViewModel handles every event and reports every outcome
@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class LoanDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val fixedNow: Instant = Instant.parse("2026-11-23T07:05:00Z")
    private val clock = FakeClock(fixedNow)
    private val loans = FakeLoanRepository()
    private val transactions = FakeTransactionRepository()
    private val atomic = PassthroughAtomicRunner()

    private fun viewModel(loanId: Long): LoanDetailViewModel =
        LoanDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("loanId" to loanId)),
            observeLoans = ObserveLoansUseCase(loans, transactions),
            wallets = FakeWalletRepository(transactions),
            commands =
                LoanCommands(
                    repaymentUseCase = RecordRepaymentUseCase(loans, transactions, atomic, clock),
                    writeOffUseCase = WriteOffLoanUseCase(loans, clock),
                    dueDateUseCase = ChangeLoanDueDateUseCase(loans, clock),
                    deleteUseCase = DeleteLoanUseCase(loans, transactions, atomic),
                ),
            clock = clock,
        )

    private suspend fun insertLoan(): Long =
        loans
            .insert(
                Loan(
                    loanId = LoanId(0L),
                    agreement =
                        Agreement(
                            direction = LoanDirection.LENT,
                            personName = "Budi",
                            principal = Money(300_000L),
                            startDate = fixedNow,
                            dueDate = null,
                            note = "",
                        ),
                    status = LoanStatus.OPEN,
                    closedAt = null,
                    audit = Audit(fixedNow, fixedNow),
                ),
            ).value

    private suspend fun LoanDetailViewModel.loaded(): LoanDetailUiState = state.first { it.isLoaded }

    private suspend fun loanOf(id: Long): Loan? = loans.observeAll().first().firstOrNull { it.loanId.value == id }

    @Test
    fun `given an open loan when opened then it is loaded and can be repaid or written off (FR-152, FR-155)`() =
        runBlocking {
            val id = insertLoan()
            val state = viewModel(id).loaded()

            assertThat(
                state.entry
                    ?.loan
                    ?.loanId
                    ?.value,
            ).isEqualTo(id)
            assertThat(state.canRepay).isTrue()
            assertThat(state.canWriteOff).isTrue()
        }

    @Test
    fun `given an unknown loan when opened then no entry is shown and nothing can be done (FR-153)`() =
        runBlocking {
            val state = viewModel(999L).loaded()

            assertThat(state.entry).isNull()
            assertThat(state.canRepay).isFalse()
            assertThat(state.canWriteOff).isFalse()
        }

    @Test
    fun `given the keypad when digits are typed and erased then the amount follows (FR-152)`() =
        runBlocking {
            val model = viewModel(insertLoan())
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
            val model = viewModel(insertLoan())
            model.loaded()

            repeat(16) { model.onEvent(LoanDetailEvent.DigitTyped("9")) }

            assertThat(model.state.value.repayment.rupiahDigits.length).isEqualTo(15)
        }

    @Test
    fun `given a wallet picked when repaying then the wallet is kept on the form (FR-152)`() =
        runBlocking {
            val model = viewModel(insertLoan())
            model.loaded()

            model.onEvent(LoanDetailEvent.WalletPicked(WalletId(4L)))

            assertThat(model.state.value.repayment.walletId).isEqualTo(WalletId(4L))
        }

    @Test
    fun `given an empty form when saved then the amount and wallet are reported and the sheet stays (FR-152)`() =
        runBlocking {
            val model = viewModel(insertLoan())
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
            val model = viewModel(insertLoan())
            model.loaded()
            model.onEvent(LoanDetailEvent.OpenRepayment)
            "999999".forEach { model.onEvent(LoanDetailEvent.DigitTyped(it.toString())) }
            model.onEvent(LoanDetailEvent.WalletPicked(WalletId(1L)))

            model.onEvent(LoanDetailEvent.SaveRepayment)

            assertThat(model.state.value.repayment.errors).containsOnly(RepaymentError.AMOUNT_ABOVE_OUTSTANDING)
            assertThat(transactions.observeAll().first()).isEmpty()
        }

    @Test
    fun `given a valid repayment when saved then it is stored and the sheet closes (FR-152)`() =
        runBlocking {
            val model = viewModel(insertLoan())
            model.loaded()
            model.onEvent(LoanDetailEvent.OpenRepayment)
            "100000".forEach { model.onEvent(LoanDetailEvent.DigitTyped(it.toString())) }
            model.onEvent(LoanDetailEvent.WalletPicked(WalletId(1L)))

            model.onEvent(LoanDetailEvent.SaveRepayment)

            val state = model.state.value
            assertThat(state.overlay.sheet).isNull()
            assertThat(state.repayment.rupiahDigits).isEqualTo("")
            assertThat(transactions.observeAll().first()).hasSize(1)
        }

    @Test
    fun `given the due date sheet when a due date is picked then the loan gets it and the sheet closes (FR-158)`() =
        runBlocking {
            val id = insertLoan()
            val model = viewModel(id)
            model.loaded()

            model.onEvent(LoanDetailEvent.OpenDueDate)
            assertThat(model.state.value.overlay.sheet).isEqualTo(LoanDetailSheet.DUE_DATE)
            model.onEvent(LoanDetailEvent.DuePicked(LoanDueOption.ONE_WEEK))

            assertThat(loanOf(id)?.agreement?.dueDate).isNotNull()
            assertThat(model.state.value.overlay.sheet).isNull()
        }

    @Test
    fun `given the due date sheet when dismissed then no sheet stays open (FR-158)`() =
        runBlocking {
            val model = viewModel(insertLoan())
            model.loaded()
            model.onEvent(LoanDetailEvent.OpenDueDate)

            model.onEvent(LoanDetailEvent.DismissSheet)

            assertThat(model.state.value.overlay.sheet).isNull()
        }

    @Test
    fun `given a write-off dialog when confirmed then the loan is written off (FR-155)`() =
        runBlocking {
            val id = insertLoan()
            val model = viewModel(id)
            model.loaded()
            model.onEvent(LoanDetailEvent.DialogOpened(LoanDialog.WRITE_OFF))
            assertThat(model.state.value.overlay.dialog).isEqualTo(LoanDialog.WRITE_OFF)

            model.onEvent(LoanDetailEvent.DialogConfirmed)

            assertThat(loanOf(id)?.status).isEqualTo(LoanStatus.WRITTEN_OFF)
            assertThat(model.state.value.overlay.dialog).isNull()
            assertThat(model.state.value.finished).isFalse()
        }

    @Test
    fun `given a delete dialog when confirmed then the loan is removed and the screen finishes (BR-046)`() =
        runBlocking {
            val id = insertLoan()
            val model = viewModel(id)
            model.loaded()
            model.onEvent(LoanDetailEvent.DialogOpened(LoanDialog.DELETE))

            model.onEvent(LoanDetailEvent.DialogConfirmed)

            assertThat(loanOf(id)).isNull()
            assertThat(model.state.value.finished).isTrue()
        }

    @Test
    fun `given a dialog when dismissed then it closes and nothing changes (FR-155)`() =
        runBlocking {
            val id = insertLoan()
            val model = viewModel(id)
            model.loaded()
            model.onEvent(LoanDetailEvent.DialogOpened(LoanDialog.DELETE))

            model.onEvent(LoanDetailEvent.DialogDismissed)
            model.onEvent(LoanDetailEvent.DialogConfirmed)

            assertThat(model.state.value.overlay.dialog).isNull()
            assertThat(loanOf(id)?.status).isEqualTo(LoanStatus.OPEN)
        }

    @Test
    fun `given a repayment already stored when repaying again then its wallet is preselected (FR-152)`() =
        runBlocking {
            val id = insertLoan()
            val model = viewModel(id)
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
            val model = viewModel(999L)
            model.loaded()

            model.onEvent(LoanDetailEvent.OpenRepayment)

            assertThat(model.state.value.overlay.sheet).isEqualTo(LoanDetailSheet.REPAYMENT)
            assertThat(model.state.value.repayment.walletId).isNull()
        }

    @Test
    fun `given several loans when one is opened then only its entry is shown (FR-153)`() =
        runBlocking {
            insertLoan()
            val second = insertLoan()

            val state = viewModel(second).loaded()

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
            val id = insertLoan()
            val model = viewModel(id)
            model.loaded()

            model.onEvent(LoanDetailEvent.DialogConfirmed)

            assertThat(loanOf(id)?.status).isEqualTo(LoanStatus.OPEN)
            assertThat(model.state.value.finished).isFalse()
        }
}
