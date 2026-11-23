package com.meqlause.fintrack.core.domain.reminder

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.loan.LoanEntry
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
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// FR-154, BR-045: a loan reminder is posted on D-3 and D-1 of an open loan that still has money outstanding

/** Loan reminders tests. */
class LoanRemindersTest {
    private val zone = ZoneId.of("Asia/Jakarta")
    private val today = LocalDate.parse("2026-11-23")

    /** FR-154: given a loan due one day ahead when collected then one reminder with lead one. */
    @Test
    fun givenLoanDueOneDayAheadWhenCollectedThenOneReminderWithLeadOne() {
        val reminders = LoanReminders.collect(listOf(entry(dueOn = "2026-11-24")), today, zone)
        assertThat(reminders).hasSize(1)
        assertThat(reminders.single().daysBefore).isEqualTo(1)
        assertThat(reminders.single().dueDate).isEqualTo(LocalDate.parse("2026-11-24"))
    }

    /** FR-154: given a loan due three days ahead when collected then lead is three. */
    @Test
    fun givenLoanDueThreeDaysAheadWhenCollectedThenLeadIsThree() {
        val reminders = LoanReminders.collect(listOf(entry(dueOn = "2026-11-26")), today, zone)
        assertThat(reminders.single().daysBefore).isEqualTo(3)
    }

    /** FR-154: given a loan due on another day when collected then nothing. */
    @Test
    fun givenLoanDueOnOtherDayWhenCollectedThenNothing() {
        assertThat(LoanReminders.collect(listOf(entry(dueOn = "2026-11-30")), today, zone)).isEmpty()
    }

    /** FR-154: given a loan without due date when collected then nothing. */
    @Test
    fun givenLoanWithoutDueDateWhenCollectedThenNothing() {
        assertThat(LoanReminders.collect(listOf(entry(dueOn = null)), today, zone)).isEmpty()
    }

    /** FR-154: given a fully repaid loan when collected then nothing. */
    @Test
    fun givenFullyRepaidLoanWhenCollectedThenNothing() {
        val repaid = entry(dueOn = "2026-11-24", repaid = 100_000L)
        assertThat(LoanReminders.collect(listOf(repaid), today, zone)).isEmpty()
    }

    /** FR-154: given a settled loan when collected then nothing. */
    @Test
    fun givenSettledLoanWhenCollectedThenNothing() {
        val settled = entry(dueOn = "2026-11-24", status = LoanStatus.SETTLED)
        assertThat(LoanReminders.collect(listOf(settled), today, zone)).isEmpty()
    }

    /** FR-154: given a borrowed loan when collected then the reminder keeps direction, name and outstanding. */
    @Test
    fun givenBorrowedLoanWhenCollectedThenReminderKeepsDirectionNameAndOutstanding() {
        val borrowed = entry(dueOn = "2026-11-24", direction = LoanDirection.BORROWED, repaid = 40_000L)
        val reminder = LoanReminders.collect(listOf(borrowed), today, zone).single()
        assertThat(reminder.direction).isEqualTo(LoanDirection.BORROWED)
        assertThat(reminder.personName).isEqualTo("Andi")
        assertThat(reminder.outstanding).isEqualTo(Money(60_000L))
        assertThat(reminder.loanId).isEqualTo(LoanId(7L))
    }

    private fun entry(
        dueOn: String?,
        repaid: Long = 0L,
        status: LoanStatus = LoanStatus.OPEN,
        direction: LoanDirection = LoanDirection.LENT,
    ): LoanEntry {
        val due = dueOn?.let { LocalDate.parse(it).atStartOfDay(zone).toInstant() }
        val stamp = Instant.parse("2026-11-01T00:00:00Z")
        val agreement = Agreement(direction, "Andi", Money(100_000L), stamp, due, "")
        val loan = Loan(LoanId(7L), agreement, status, null, Audit(stamp, stamp))
        val movements = if (repaid > 0L) listOf(repayment(repaid)) else emptyList()
        return LoanEntry(loan, movements)
    }

    private fun repayment(rupiah: Long): Transaction {
        val stamp = Instant.parse("2026-11-10T00:00:00Z")
        return Transaction(
            transactionId = TransactionId(1L),
            movement = Movement(TransactionType.REPAY, Money(rupiah)),
            routing = Routing(WalletId(1L), null, null, LoanId(7L)),
            memo = Memo(),
            origin = Origin(TransactionSource.MANUAL, null),
            timing = Timing(stamp, Audit(stamp, stamp)),
        )
    }
}
