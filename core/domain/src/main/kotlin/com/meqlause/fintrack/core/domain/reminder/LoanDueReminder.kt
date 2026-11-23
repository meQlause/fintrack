package com.meqlause.fintrack.core.domain.reminder

import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.Money
import java.time.LocalDate

/** FR-154, BR-045: an open loan with money outstanding that falls due in a lead day */
public data class LoanDueReminder(
    public val loanId: LoanId,
    public val personName: String,
    public val direction: LoanDirection,
    public val outstanding: Money,
    public val dueDate: LocalDate,
    public val daysBefore: Int,
) : Reminder {
    override val reminderKey: String
        get() = "loan-${loanId.value}-$dueDate-$daysBefore"
}
