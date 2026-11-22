package com.meqlause.fintrack.core.data.mapper

import com.meqlause.fintrack.core.database.entity.LoanEntity
import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.Money

// DOC-02, FR-150: maps a loan row to the domain model
internal fun LoanEntity.toLoan(): Loan =
    Loan(
        loanId = LoanId(loanId),
        agreement =
            Agreement(
                direction = terms.direction,
                personName = terms.personName,
                principal = Money(terms.principal),
                startDate = terms.startDate.toInstant(),
                dueDate = terms.dueDate?.toInstant(),
                note = terms.note,
            ),
        status = status,
        closedAt = closedAt?.toInstant(),
        audit = audit.toAudit(),
    )

// DOC-02, FR-150: maps a loan to its row
internal fun Loan.toEntity(): LoanEntity =
    LoanEntity(
        loanId = loanId.value,
        terms =
            LoanEntity.Terms(
                direction = agreement.direction,
                personName = agreement.personName,
                principal = agreement.principal.rupiah,
                startDate = agreement.startDate.toEpochMilli(),
                dueDate = agreement.dueDate?.toEpochMilli(),
                note = agreement.note,
            ),
        status = status,
        closedAt = closedAt?.toEpochMilli(),
        audit = audit.toColumns(),
    )
