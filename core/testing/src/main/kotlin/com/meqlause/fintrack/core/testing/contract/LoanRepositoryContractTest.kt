package com.meqlause.fintrack.core.testing.contract

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.testing.data.aLoan
import com.meqlause.fintrack.core.testing.data.anInstant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

/** DOC-03, FR-150: the contract of LoanRepository, run against the fake and the Local implementation. */
abstract class LoanRepositoryContractTest {
    private val fixture: RepositoryFixture by lazy { createFixture() }

    /** ADR-007: a fresh, empty store for one test. */
    protected abstract fun createFixture(): RepositoryFixture

    /** ADR-007: releases the store after each test. */
    @After
    fun closeFixture() {
        fixture.close()
    }

    /** DOC-02: a fresh store has no loans. */
    @Test
    fun givenNoLoansWhenObservedThenTheListIsEmpty() =
        runTest {
            assertThat(fixture.loans.observeAll().first()).isEmpty()
        }

    /** FR-150: an open loan with a due date and a note keeps every agreement field. */
    @Test
    fun givenOpenLoanWithDueDateWhenObservedThenTheAgreementIsKept() =
        runTest {
            val loan =
                aLoan(personName = "Sari").let {
                    it.copy(
                        agreement =
                            it.agreement.copy(
                                dueDate = anInstant(offsetMillis = 86_400_000L),
                                note = "Pinjam motor",
                            ),
                    )
                }

            val loanId = fixture.loans.insert(loan)

            assertThat(fixture.loans.observeAll().first()).isEqualTo(listOf(loan.copy(loanId = loanId)))
        }

    /** FR-150, BR-041: a settled borrowed loan without due date keeps its status and close time. */
    @Test
    fun givenSettledBorrowedLoanWhenObservedThenStatusAndClosedAtAreKept() =
        runTest {
            val loan =
                aLoan(direction = LoanDirection.BORROWED).let {
                    it.copy(status = LoanStatus.SETTLED, closedAt = anInstant(offsetMillis = 7_000L))
                }

            val loanId = fixture.loans.insert(loan)

            assertThat(fixture.loans.observeAll().first()).isEqualTo(listOf(loan.copy(loanId = loanId)))
        }
}
