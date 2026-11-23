package com.meqlause.fintrack.feature.loan.list

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.loan.LoanEntry
import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// FR-153, FR-154: a loan becomes one list row, with its due date and overdue flag, and rows group by person
class LoanListMappingTest {
    private val zone = ZoneId.of("Asia/Jakarta")
    private val today = LocalDate.of(2026, 11, 23)
    private val nowStamp = Instant.parse("2026-11-01T00:00:00Z")

    private fun entry(
        person: String,
        principal: Long,
        status: LoanStatus = LoanStatus.OPEN,
        dueDate: Instant? = null,
    ): LoanEntry =
        LoanEntry(
            loan =
                Loan(
                    loanId = LoanId(1L),
                    agreement =
                        Agreement(
                            direction = LoanDirection.LENT,
                            personName = person,
                            principal = Money(principal),
                            startDate = nowStamp,
                            dueDate = dueDate,
                            note = "",
                        ),
                    status = status,
                    closedAt = null,
                    audit = Audit(nowStamp, nowStamp),
                ),
            movements = emptyList(),
        )

    @Test
    fun givenAnOpenLoanPastItsDueDateWhenMappedThenItIsOverdue() {
        val loanRow = entry("Budi", 300_000L, dueDate = Instant.parse("2026-11-10T00:00:00Z")).toListRow(today, zone)

        assertThat(loanRow.dueInfo.overdue).isTrue()
        assertThat(loanRow.dueInfo.date).isEqualTo(LocalDate.of(2026, 11, 10))
        assertThat(loanRow.personName).isEqualTo("Budi")
        assertThat(loanRow.status).isEqualTo(LoanStatus.OPEN)
    }

    @Test
    fun givenASettledLoanPastItsDueDateWhenMappedThenItIsNotOverdue() {
        val loanRow =
            entry("Sari", 300_000L, LoanStatus.SETTLED, dueDate = Instant.parse("2026-11-10T00:00:00Z"))
                .toListRow(today, zone)

        assertThat(loanRow.dueInfo.overdue).isFalse()
    }

    @Test
    fun givenALoanWithoutDueDateWhenMappedThenItHasNoDateAndIsNotOverdue() {
        val loanRow = entry("Dimas", 300_000L).toListRow(today, zone)

        assertThat(loanRow.dueInfo.date).isNull()
        assertThat(loanRow.dueInfo.overdue).isFalse()
        assertThat(loanRow.progress).isEqualTo(0f)
    }

    @Test
    fun givenAZeroPrincipalWhenMappedThenTheProgressIsZero() {
        val loanRow = entry("Ani", 0L).toListRow(today, zone)

        assertThat(loanRow.progress).isEqualTo(0f)
    }

    @Test
    fun givenTwoGroupsWhenComparedThenOnlyTheSamePersonAndRowsAreEqual() {
        val loanRow = entry("Budi", 1L).toListRow(today, zone)
        val group = LoanPersonGroup(personName = "Budi", rows = listOf(loanRow))

        assertThat(group).isEqualTo(LoanPersonGroup(personName = "Budi", rows = listOf(loanRow)))
        assertThat(group.hashCode()).isEqualTo(LoanPersonGroup(personName = "Budi", rows = listOf(loanRow)).hashCode())
        assertThat(group).isNotEqualTo(LoanPersonGroup(personName = "Sari", rows = listOf(loanRow)))
        assertThat(group).isNotEqualTo(LoanPersonGroup(personName = "Budi", rows = emptyList()))
    }

    @Test
    fun givenRowsOfSeveralPeopleWhenGroupedThenEachPersonHasOneGroupInOrder() {
        val rows =
            listOf(
                entry("Budi", 1L).toListRow(today, zone),
                entry("Sari", 1L).toListRow(today, zone),
                entry("Budi", 2L).toListRow(today, zone),
            )

        val groups = rows.groupedByPerson()

        assertThat(groups.map { it.personName }).containsExactly("Budi", "Sari")
        assertThat(groups.first().rows).containsExactly(rows[0], rows[2])
    }
}
