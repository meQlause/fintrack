package com.meqlause.fintrack.feature.loan.list

import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import java.time.LocalDate

// FR-153, S-17: one loan as the list shows it; amounts stay values until the screen formats them
internal data class LoanListRow(
    val loanId: Long,
    val personName: String,
    val outstanding: Money,
    val progress: Float,
    val dueInfo: LoanDue,
    val status: LoanStatus,
)

// FR-154, FR-158: the due date of a loan, and whether an open loan is already past it
internal data class LoanDue(
    val date: LocalDate?,
    val overdue: Boolean,
)
