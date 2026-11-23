package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
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
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// FR-158, BR-046: an open loan can get a new due date, and deleting a loan removes its own movements only

/** FR-158, BR-046: change loan due date and delete loan use case tests. */
class ChangeLoanDueDateAndDeleteLoanTest {
    private val zone = ZoneId.of("Asia/Jakarta")
    private val clock = Clock.fixed(Instant.parse("2026-11-23T02:00:00Z"), zone)
    private val loans = MemoryLoanRepository()
    private val transactions = MemoryTransactionRepository()
    private val change = ChangeLoanDueDateUseCase(loans, clock)
    private val delete = DeleteLoanUseCase(loans, transactions, ImmediateAtomicRunner())

    /** FR-158: a missing loan is refused and nothing changes. */
    @Test
    fun givenUnknownLoanWhenDueDateChangedThenFalse() {
        runBlocking {
            assertThat(change(LoanId(9L), LocalDate.of(2026, 12, 1))).isFalse()
        }
    }

    /** FR-158: a settled loan keeps its due date. */
    @Test
    fun givenSettledLoanWhenDueDateChangedThenRefused() {
        runBlocking {
            val loanKey = loans.insert(loan(LoanStatus.SETTLED))
            assertThat(change(loanKey, LocalDate.of(2026, 12, 1))).isFalse()
            assertThat(
                loans
                    .observeAll()
                    .first()
                    .single()
                    .agreement.dueDate,
            ).isNull()
        }
    }

    /** FR-158: an open loan takes the new date at the start of that day in the local zone, and null clears it. */
    @Test
    fun givenOpenLoanWhenDueDateChangedThenDateIsStoredAndCanBeCleared() {
        runBlocking {
            val loanKey = loans.insert(loan(LoanStatus.OPEN))
            assertThat(change(loanKey, LocalDate.of(2026, 12, 1))).isTrue()
            val stored = loans.observeAll().first().single()
            assertThat(stored.agreement.dueDate).isEqualTo(Instant.parse("2026-11-30T17:00:00Z"))
            assertThat(change(loanKey, null)).isTrue()
            assertThat(
                loans
                    .observeAll()
                    .first()
                    .single()
                    .agreement.dueDate,
            ).isNull()
        }
    }

    /** BR-046: deleting a loan removes its own movements and leaves other transactions untouched. */
    @Test
    fun givenLoanWithMovementsWhenDeletedThenOnlyItsMovementsAreRemoved() {
        runBlocking {
            val mine = loans.insert(loan(LoanStatus.OPEN))
            val other = loans.insert(loan(LoanStatus.OPEN))
            transactions.insert(movement(mine))
            transactions.insert(movement(mine))
            transactions.insert(movement(other))
            delete(mine)
            assertThat(loans.observeAll().first().map { it.loanId }).isEqualTo(listOf(other))
            assertThat(transactions.observeAll().first()).hasSize(1)
        }
    }

    private fun loan(status: LoanStatus): Loan =
        Loan(
            loanId = LoanId(0L),
            agreement =
                Agreement(
                    direction = LoanDirection.LENT,
                    personName = "Andi",
                    principal = Money(100_000L),
                    startDate = Instant.parse("2026-11-20T00:00:00Z"),
                    dueDate = null,
                    note = "",
                ),
            status = status,
            closedAt = null,
            audit = Audit(Instant.EPOCH, Instant.EPOCH),
        )

    private fun movement(loanId: LoanId): Transaction =
        Transaction(
            transactionId = TransactionId(0L),
            movement = Movement(TransactionType.LEND, Money(100_000L)),
            routing = Routing(WalletId(1L), null, categoryId = null, loanId = loanId),
            memo = Memo(),
            origin = Origin(TransactionSource.MANUAL, null),
            timing = Timing(Instant.EPOCH, Audit(Instant.EPOCH, Instant.EPOCH)),
        )
}
