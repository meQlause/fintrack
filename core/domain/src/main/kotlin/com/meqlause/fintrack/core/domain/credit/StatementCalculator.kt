package com.meqlause.fintrack.core.domain.credit

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.StatementStatus
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

/** BR-031 to BR-034, BR-036, BR-044: statement amounts, status and available limit. Pure; callers pass today. */
public object StatementCalculator {
    /** BR-031: the balance on [date], counting every entry dated on or before that day. */
    public fun balanceOn(
        account: CreditAccount,
        date: LocalDate,
    ): Money =
        account.entries
            .filter { !it.date.isAfter(date) }
            .fold(account.initialBalance) { total, entry -> total + entry.delta }

    /** BR-031: outstanding = max(0, -balance); a positive balance is an overpayment. */
    public fun outstandingOn(
        account: CreditAccount,
        date: LocalDate,
    ): Money = Money(maxOf(0L, -balanceOn(account, date).rupiah))

    /** BR-044: creditLimit - outstanding; negative when the wallet is over its limit. */
    public fun availableLimit(
        account: CreditAccount,
        date: LocalDate,
    ): Money = account.terms.creditLimit - outstandingOn(account, date)

    /** BR-036: unbilled instalment principal at the cut-off of [month]; later purchases are excluded. */
    public fun unbilledInstalments(
        account: CreditAccount,
        month: YearMonth,
    ): Money {
        val cutOff = StatementDates.cutOff(month, account.terms.statementDay)
        return account.instalments
            .filter { !it.purchaseDate.isAfter(cutOff) }
            .fold(Money(0L)) { total, plan -> total + unbilledOf(plan, month) }
    }

    /** BR-032, BR-033, BR-034: the statement for [month] as seen on [today]. */
    public fun summary(
        account: CreditAccount,
        month: YearMonth,
        today: LocalDate,
    ): StatementSummary {
        val terms = account.terms
        val cutOff = StatementDates.cutOff(month, terms.statementDay)
        val period =
            StatementPeriod(
                month = month,
                cutOff = cutOff,
                periodStart = StatementDates.periodStart(month, terms.statementDay),
                dueDate = StatementDates.dueDate(month, terms.statementDay, terms.dueDay),
            )
        val breakdown =
            StatementAmounts(
                outstanding = outstandingOn(account, cutOff),
                unbilledInstalments = unbilledInstalments(account, month),
                paidSinceCutoff = paidSince(account, cutOff, today),
            )
        val issuedFrom = StatementDates.firstStatementMonth(account.createdOn, terms.statementDay)
        return StatementSummary(period, breakdown, statusOf(period, breakdown, issuedFrom, today))
    }

    /** BR-033, BR-034: the bill card statement, the latest issued one or the first statement of a new wallet. */
    public fun current(
        account: CreditAccount,
        today: LocalDate,
    ): StatementSummary {
        val statementDay = account.terms.statementDay
        val firstMonth = StatementDates.firstStatementMonth(account.createdOn, statementDay)
        val month = maxOf(firstMonth, StatementDates.latestCutOffMonth(today, statementDay))
        return summary(account, month, today)
    }

    /** BR-034: NOT_ISSUED before the first cut-off, then PAID, DUE up to the due date, OVERDUE after it. */
    private fun statusOf(
        period: StatementPeriod,
        breakdown: StatementAmounts,
        issuedFrom: YearMonth,
        today: LocalDate,
    ): StatementStatus =
        when {
            period.month < issuedFrom || today.isBefore(period.cutOff) -> StatementStatus.NOT_ISSUED
            breakdown.remainingDue.isZero -> StatementStatus.PAID
            !today.isAfter(period.dueDate) -> StatementStatus.DUE
            else -> StatementStatus.OVERDUE
        }

    /** BR-033: transfers into the wallet after the cut-off and up to today. */
    private fun paidSince(
        account: CreditAccount,
        cutOff: LocalDate,
        today: LocalDate,
    ): Money =
        account.entries
            .filter { it.isTransferIn && it.date.isAfter(cutOff) && !it.date.isAfter(today) }
            .fold(Money(0L)) { total, entry -> total + entry.delta }

    /** BR-035, BR-036: portions 1..tenor-1 are the base amount and the last one takes the remainder. */
    private fun unbilledOf(
        plan: CreditInstalment,
        month: YearMonth,
    ): Money {
        val principal = plan.principal.rupiah
        val base = principal / plan.tenorMonths
        val billedPortions = plan.firstStatementMonth.until(month, ChronoUnit.MONTHS) + 1
        val billed = if (billedPortions >= plan.tenorMonths) principal else base * maxOf(0L, billedPortions)
        return Money(principal - billed)
    }
}
