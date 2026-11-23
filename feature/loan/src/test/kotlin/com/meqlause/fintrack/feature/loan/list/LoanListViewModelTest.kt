package com.meqlause.fintrack.feature.loan.list

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.usecase.ObserveLoansUseCase
import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeLoanRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.time.FakeClock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import java.time.Instant

// FR-153, FR-154, S-17: the loan list groups the open loans by person and shows the settled ones apart
@OptIn(ExperimentalCoroutinesApi::class)
class LoanListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val fixedNow: Instant = Instant.parse("2026-11-23T07:05:00Z")
    private val clock = FakeClock(fixedNow)
    private val loans = FakeLoanRepository()
    private val transactions = FakeTransactionRepository()

    private suspend fun addLoan(
        person: String,
        direction: LoanDirection,
        status: LoanStatus,
        dueDate: Instant? = null,
    ) {
        loans.insert(
            Loan(
                loanId = LoanId(0L),
                agreement =
                    Agreement(
                        direction = direction,
                        personName = person,
                        principal = Money(300_000L),
                        startDate = fixedNow,
                        dueDate = dueDate,
                        note = "",
                    ),
                status = status,
                closedAt = null,
                audit = Audit(fixedNow, fixedNow),
            ),
        )
    }

    private fun viewModel(): LoanListViewModel = LoanListViewModel(ObserveLoansUseCase(loans, transactions), clock)

    @Test
    fun `given lent loans when listed then open ones are grouped by person and settled ones apart (FR-153)`() =
        runBlocking {
            addLoan("Budi", LoanDirection.LENT, LoanStatus.OPEN)
            addLoan("Sari", LoanDirection.LENT, LoanStatus.SETTLED)
            addLoan("Ani", LoanDirection.BORROWED, LoanStatus.OPEN)

            val state = viewModel().state.first { it.groups.isNotEmpty() }

            assertThat(state.direction).isEqualTo(LoanDirection.LENT)
            assertThat(state.groups.map { it.personName }).isEqualTo(listOf("Budi"))
            assertThat(state.settled.map { it.personName }).isEqualTo(listOf("Sari"))
        }

    @Test
    fun `given a loan past its due date when listed then it is marked overdue (FR-154)`() =
        runBlocking {
            addLoan("Budi", LoanDirection.LENT, LoanStatus.OPEN, dueDate = Instant.parse("2026-11-01T00:00:00Z"))
            addLoan("Dimas", LoanDirection.LENT, LoanStatus.OPEN, dueDate = null)

            val state = viewModel().state.first { it.groups.isNotEmpty() }
            val rows = state.groups.flatMap { it.rows }.associateBy { it.personName }

            assertThat(rows.getValue("Budi").dueInfo.overdue).isTrue()
            assertThat(rows.getValue("Budi").dueInfo.date).isEqualTo(
                Instant.parse("2026-11-01T00:00:00Z").atZone(clock.zone).toLocalDate(),
            )
            assertThat(rows.getValue("Dimas").dueInfo.overdue).isFalse()
            assertThat(rows.getValue("Dimas").dueInfo.date).isNull()
        }

    @Test
    fun `given borrowed loans when the direction is picked then the list follows it (FR-153)`() =
        runBlocking {
            addLoan("Budi", LoanDirection.LENT, LoanStatus.OPEN)
            addLoan("Ani", LoanDirection.BORROWED, LoanStatus.OPEN)
            val model = viewModel()
            model.state.first { it.groups.isNotEmpty() }

            model.pickDirection(LoanDirection.BORROWED)

            val state = model.state.first { it.direction == LoanDirection.BORROWED }
            assertThat(state.groups.map { it.personName }).isEqualTo(listOf("Ani"))
            assertThat(state.groups.single().rows).hasSize(1)
            assertThat(state.settled).hasSize(0)
        }
}
