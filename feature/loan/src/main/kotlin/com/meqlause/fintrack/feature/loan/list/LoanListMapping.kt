package com.meqlause.fintrack.feature.loan.list

import com.meqlause.fintrack.core.domain.loan.LoanBalance
import com.meqlause.fintrack.core.domain.loan.LoanEntry
import com.meqlause.fintrack.core.model.LoanStatus
import java.time.LocalDate
import java.time.ZoneId

// FR-153, FR-154: one loan becomes a row; an open loan past its due date is overdue
internal fun LoanEntry.toListRow(
    today: LocalDate,
    zone: ZoneId,
): LoanListRow {
    val dueOn =
        loan.agreement.dueDate
            ?.atZone(zone)
            ?.toLocalDate()
    val isOpen = loan.status == LoanStatus.OPEN
    return LoanListRow(
        loanId = loan.loanId.value,
        personName = loan.agreement.personName,
        outstanding = loanBalance.outstanding,
        progress = progressOf(loanBalance),
        due = LoanDue(date = dueOn, overdue = isOpen && dueOn != null && dueOn.isBefore(today)),
        status = loan.status,
    )
}

// FR-153: the open loans of each person, in the order the list already holds them
internal fun List<LoanListRow>.groupedByPerson(): List<LoanPersonGroup> =
    groupBy { it.personName }.map { (name, rows) -> LoanPersonGroup(personName = name, rows = rows) }

// FR-153: the share of the principal already repaid, between 0 and 1 for the progress bar
private fun progressOf(balance: LoanBalance): Float =
    if (balance.principal.rupiah <= 0L) {
        0f
    } else {
        (balance.repaid.rupiah.toFloat() / balance.principal.rupiah).coerceIn(0f, 1f)
    }
