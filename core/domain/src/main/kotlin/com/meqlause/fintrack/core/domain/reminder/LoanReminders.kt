package com.meqlause.fintrack.core.domain.reminder

import com.meqlause.fintrack.core.domain.loan.LoanEntry
import com.meqlause.fintrack.core.model.LoanStatus
import java.time.LocalDate
import java.time.ZoneId

/** FR-154, BR-045: open loans with a due date and money outstanding, due in a lead day */
public object LoanReminders {
    /** FR-154: one reminder per open loan whose due date is D-3 or D-1 and whose outstanding is above zero. */
    public fun collect(
        entries: List<LoanEntry>,
        today: LocalDate,
        zone: ZoneId,
    ): List<LoanDueReminder> = entries.mapNotNull { reminderOf(it, today, zone) }

    private fun reminderOf(
        entry: LoanEntry,
        today: LocalDate,
        zone: ZoneId,
    ): LoanDueReminder? {
        val loan = entry.loan
        val outstanding = entry.loanBalance.outstanding
        val dueDate =
            loan.agreement.dueDate
                ?.atZone(zone)
                ?.toLocalDate()
        val isOpen = loan.status == LoanStatus.OPEN && outstanding.rupiah > 0L
        val lead = dueDate?.let { ReminderRules.leadOf(it, today) }.takeIf { isOpen }
        if (dueDate == null || lead == null) return null
        return LoanDueReminder(
            loan.loanId,
            loan.agreement.personName,
            loan.agreement.direction,
            outstanding,
            dueDate,
            lead,
        )
    }
}
