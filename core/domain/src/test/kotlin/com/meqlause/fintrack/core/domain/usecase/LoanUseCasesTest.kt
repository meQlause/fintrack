package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.loan.LoanDraft
import com.meqlause.fintrack.core.domain.loan.RepaymentError
import com.meqlause.fintrack.core.domain.loan.RepaymentResult
import com.meqlause.fintrack.core.domain.transaction.SaveTransactionResult
import com.meqlause.fintrack.core.domain.transaction.TransactionError
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

// FR-150 … FR-155, BR-040 … BR-042: loans are saved, repaid and written off through their use cases
class LoanUseCasesTest {
    private val clock = Clock.fixed(Instant.parse("2026-11-23T02:00:00Z"), ZoneId.of("Asia/Jakarta"))
    private val loans = MemoryLoanRepository()
    private val transactions = MemoryTransactionRepository()
    private val wallet = WalletId(1L)
    private val save = SaveLoanUseCase(loans, transactions, ImmediateAtomicRunner(), clock)
    private val repay = RecordRepaymentUseCase(loans, transactions, ImmediateAtomicRunner(), clock)
    private val writeOff = WriteOffLoanUseCase(loans, clock)
    private val observe = ObserveLoansUseCase(loans, transactions)

    // FR-150: a LENT loan stores an OPEN loan and one LEND movement with the loan id and no category (BR-040)
    @Test
    fun givenLentDraftWhenSavedThenOpenLoanAndLendMovementAreStored() {
        runBlocking {
            val saved = save(draft(LoanDirection.LENT, 100_000L)) as SaveTransactionResult.Saved
            val loan = loans.observeAll().first().single()
            val movement = transactions.observeAll().first().single()
            assertThat(loan.status).isEqualTo(LoanStatus.OPEN)
            assertThat(movement.movement.type).isEqualTo(TransactionType.LEND)
            assertThat(movement.routing.loanId).isEqualTo(loan.loanId)
            assertThat(movement.routing.categoryId).isNull()
            assertThat(saved.transactionId).isEqualTo(movement.transactionId)
        }
    }

    // FR-151: a loan without a person name is rejected and nothing is stored
    @Test
    fun givenBlankPersonNameWhenSavedThenNothingIsStored() {
        runBlocking {
            val result = save(draft(LoanDirection.BORROWED, 100_000L, name = " ")) as SaveTransactionResult.Rejected
            assertThat(result.errors).isEqualTo(setOf(TransactionError.PERSON_REQUIRED))
            assertThat(loans.observeAll().first()).isEmpty()
            assertThat(transactions.observeAll().first()).isEmpty()
        }
    }

    // BR-041: the outstanding balance is principal minus repayments
    @Test
    fun givenPartialRepaymentWhenObservedThenOutstandingDrops() {
        runBlocking {
            val loanId = lend(100_000L)
            val result = repay(loanId, Money(40_000L), wallet)
            val entry = observe().first().single()
            assertThat(result is RepaymentResult.Saved).isTrue()
            assertThat(entry.balance.outstanding).isEqualTo(Money(60_000L))
            assertThat(entry.loan.status).isEqualTo(LoanStatus.OPEN)
        }
    }

    // BR-041: a repayment above the outstanding balance is rejected and stores nothing
    @Test
    fun givenRepaymentAboveOutstandingWhenRecordedThenRejected() {
        runBlocking {
            val loanId = lend(100_000L)
            val result = repay(loanId, Money(150_000L), wallet) as RepaymentResult.Rejected
            assertThat(result.errors).isEqualTo(setOf(RepaymentError.AMOUNT_ABOVE_OUTSTANDING))
            assertThat(transactions.observeAll().first()).hasSize(1)
        }
    }

    // BR-041: a repayment that clears the balance settles the loan and sets closedAt
    @Test
    fun givenFullRepaymentWhenRecordedThenLoanIsSettled() {
        runBlocking {
            val loanId = borrow(100_000L)
            repay(loanId, Money(100_000L), wallet)
            val loan = loans.observeAll().first().single()
            assertThat(loan.status).isEqualTo(LoanStatus.SETTLED)
            assertThat(loan.closedAt).isNotNull()
            assertThat(
                transactions
                    .observeAll()
                    .first()
                    .last()
                    .movement.type,
            ).isEqualTo(TransactionType.REPAY)
        }
    }

    // BR-042: only an open LENT loan can be written off, and no money moves
    @Test
    fun givenLentLoanWhenWrittenOffThenStatusChangesWithoutAMovement() {
        runBlocking {
            val loanId = lend(100_000L)
            val done = writeOff(loanId)
            assertThat(done).isTrue()
            assertThat(
                loans
                    .observeAll()
                    .first()
                    .single()
                    .status,
            ).isEqualTo(LoanStatus.WRITTEN_OFF)
            assertThat(transactions.observeAll().first()).hasSize(1)
        }
    }

    // BR-042: a borrowed loan is never written off by the use case
    @Test
    fun givenBorrowedLoanWhenWrittenOffThenNothingChanges() {
        runBlocking {
            val loanId = borrow(100_000L)
            val done = writeOff(loanId)
            assertThat(done).isFalse()
            assertThat(
                loans
                    .observeAll()
                    .first()
                    .single()
                    .status,
            ).isEqualTo(LoanStatus.OPEN)
        }
    }

    private suspend fun lend(amount: Long): LoanId = open(LoanDirection.LENT, amount)

    private suspend fun borrow(amount: Long): LoanId = open(LoanDirection.BORROWED, amount)

    private suspend fun open(
        direction: LoanDirection,
        amount: Long,
    ): LoanId {
        save(draft(direction, amount))
        return loans
            .observeAll()
            .first()
            .last()
            .loanId
    }

    private fun draft(
        direction: LoanDirection,
        amount: Long,
        name: String = "Andi",
    ): LoanDraft =
        LoanDraft(
            direction = direction,
            personName = name,
            principal = Money(amount),
            walletId = wallet,
            startDate = Instant.parse("2026-11-20T00:00:00Z"),
            dueDate = null,
            note = "",
        )
}
