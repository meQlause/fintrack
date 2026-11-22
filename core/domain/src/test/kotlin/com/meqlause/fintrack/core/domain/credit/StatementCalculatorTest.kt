package com.meqlause.fintrack.core.domain.credit

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.StatementStatus
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.YearMonth

/** BR-031 to BR-036, BR-044: statement status, partial payments, year boundaries and instalment billing. */
class StatementCalculatorTest {
    private val november = YearMonth.of(2026, 11)
    private val december = YearMonth.of(2026, 12)

    /** BR-034, FR-139: a new wallet shows NOT_ISSUED until its first cut-off. */
    @Test
    fun `given wallet created 10 Nov when 12 Nov then first statement is NOT_ISSUED (BR-034)`() {
        val account = CreditFixtures.account(createdOn = LocalDate.of(2026, 11, 10))

        val current = StatementCalculator.current(account, LocalDate.of(2026, 11, 12))

        assertThat(current.period.month).isEqualTo(november)
        assertThat(current.status).isEqualTo(StatementStatus.NOT_ISSUED)
    }

    /** BR-034: the cut-off day itself issues the statement. */
    @Test
    fun `given wallet created 10 Nov when 25 Nov then first statement is issued (BR-034)`() {
        val account = CreditFixtures.account(createdOn = LocalDate.of(2026, 11, 10))

        val current = StatementCalculator.current(account, LocalDate.of(2026, 11, 25))

        assertThat(current.status).isEqualTo(StatementStatus.PAID)
    }

    /** BR-034, BR-036: a wallet created after a cut-off starts with the following statement. */
    @Test
    fun `given wallet created 26 Nov when 27 Nov then current statement is Dec and NOT_ISSUED (BR-034)`() {
        val account = CreditFixtures.account(createdOn = LocalDate.of(2026, 11, 26))

        val current = StatementCalculator.current(account, LocalDate.of(2026, 11, 27))

        assertThat(current.period.month).isEqualTo(december)
        assertThat(current.status).isEqualTo(StatementStatus.NOT_ISSUED)
    }

    /** BR-034: a statement with debt is DUE up to and including the due date. */
    @Test
    fun `given debt when today equals due date then status is DUE (BR-034)`() {
        val summary =
            StatementCalculator.summary(
                CreditFixtures.workedExample().withoutPayments(),
                november,
                LocalDate.of(2026, 12, 10),
            )

        assertThat(summary.status).isEqualTo(StatementStatus.DUE)
    }

    /** BR-034: a statement with debt after the due date is OVERDUE. */
    @Test
    fun `given debt when today is after due date then status is OVERDUE (BR-034)`() {
        val summary =
            StatementCalculator.summary(
                CreditFixtures.workedExample().withoutPayments(),
                november,
                LocalDate.of(2026, 12, 11),
            )

        assertThat(summary.status).isEqualTo(StatementStatus.OVERDUE)
    }

    /** BR-033, FR-133: a partial payment lowers remainingDue and the statement stays DUE. */
    @Test
    fun `given partial payment when statement is read then remaining due is reduced and status is DUE (BR-033)`() {
        val account =
            CreditFixtures.workedExample().withoutPayments().withEntries(
                CreditFixtures.transferIn(LocalDate.of(2026, 12, 1), 400_000L),
            )

        val summary = StatementCalculator.summary(account, november, LocalDate.of(2026, 12, 5))

        assertThat(summary.breakdown.remainingDue).isEqualTo(Money(600_000L))
        assertThat(summary.breakdown.isPartiallyPaid).isTrue()
        assertThat(summary.status).isEqualTo(StatementStatus.DUE)
    }

    /** BR-034, FR-133: a partial payment on an overdue statement keeps it OVERDUE. */
    @Test
    fun `given partial payment after due date when statement is read then status is OVERDUE (BR-034)`() {
        val account =
            CreditFixtures.workedExample().withoutPayments().withEntries(
                CreditFixtures.transferIn(LocalDate.of(2026, 12, 5), 400_000L),
            )

        val summary = StatementCalculator.summary(account, november, LocalDate.of(2026, 12, 11))

        assertThat(summary.breakdown.remainingDue).isEqualTo(Money(600_000L))
        assertThat(summary.status).isEqualTo(StatementStatus.OVERDUE)
    }

    /** BR-033: a payment before the cut-off reduces outstanding but is not paidSinceCutoff. */
    @Test
    fun `given payment before cut-off when statement is read then it is not paid since cut-off (BR-033)`() {
        val account =
            CreditFixtures.workedExample().withoutPayments().withEntries(
                CreditFixtures.transferIn(LocalDate.of(2026, 11, 24), 500_000L),
            )

        val breakdown = StatementCalculator.summary(account, november, LocalDate.of(2026, 11, 25)).breakdown

        assertThat(breakdown.outstanding).isEqualTo(Money(3_000_000L))
        assertThat(breakdown.paidSinceCutoff).isEqualTo(Money(0L))
        assertThat(breakdown.remainingDue).isEqualTo(Money(500_000L))
    }

    /** BR-033, FR-133: an overpayment makes remainingDue 0 and the statement PAID. */
    @Test
    fun `given overpayment after cut-off when statement is read then status is PAID (BR-033, FR-133)`() {
        val account =
            CreditFixtures.workedExample().withoutPayments().withEntries(
                CreditFixtures.transferIn(LocalDate.of(2026, 12, 1), 1_500_000L),
            )

        val summary = StatementCalculator.summary(account, november, LocalDate.of(2026, 12, 2))

        assertThat(summary.breakdown.remainingDue).isEqualTo(Money(0L))
        assertThat(summary.breakdown.isPartiallyPaid).isFalse()
        assertThat(summary.status).isEqualTo(StatementStatus.PAID)
    }

    /** BR-031, BR-044: a positive balance is an overpayment; outstanding is 0 and the full limit is available. */
    @Test
    fun `given positive balance when outstanding is read then it is 0 and limit is fully available (BR-031, BR-044)`() {
        val account =
            CreditFixtures.account(
                entries =
                    listOf(
                        CreditFixtures.expense(LocalDate.of(2026, 11, 2), 500_000L),
                        CreditFixtures.transferIn(LocalDate.of(2026, 11, 3), 1_000_000L),
                    ),
            )
        val asOf = LocalDate.of(2026, 11, 4)

        assertThat(StatementCalculator.outstandingOn(account, asOf)).isEqualTo(Money(0L))
        assertThat(StatementCalculator.availableLimit(account, asOf)).isEqualTo(Money(10_000_000L))
    }

    /** BR-044: over the limit the available limit is negative. */
    @Test
    fun `given debt above limit when available limit is read then it is negative (BR-044)`() {
        val account =
            CreditFixtures
                .account(
                    entries = listOf(CreditFixtures.expense(LocalDate.of(2026, 11, 2), 1_500_000L)),
                ).copy(terms = CreditTerms(Money(1_000_000L), statementDay = 25, dueDay = 10))

        assertThat(StatementCalculator.availableLimit(account, LocalDate.of(2026, 11, 3)))
            .isEqualTo(Money(-500_000L))
    }

    /** BR-036: a purchase after the cut-off is not part of that statement at all. */
    @Test
    fun `given purchase after cut-off when that statement is read then the plan is excluded (BR-036)`() {
        val account =
            CreditFixtures.account(
                entries = listOf(CreditFixtures.expense(LocalDate.of(2026, 11, 26), 3_000_000L)),
                instalments =
                    listOf(
                        CreditFixtures.plan(LocalDate.of(2026, 11, 26), 3_000_000L, 6, YearMonth.of(2026, 12)),
                    ),
            )

        val novAmounts = StatementCalculator.summary(account, november, LocalDate.of(2026, 11, 25)).breakdown
        val decAmounts = StatementCalculator.summary(account, december, LocalDate.of(2026, 12, 25)).breakdown

        assertThat(novAmounts.outstanding).isEqualTo(Money(0L))
        assertThat(novAmounts.unbilledInstalments).isEqualTo(Money(0L))
        assertThat(decAmounts.unbilledInstalments).isEqualTo(Money(2_500_000L))
        assertThat(decAmounts.statementAmount).isEqualTo(Money(500_000L))
    }

    /** BR-035: the last portion absorbs the rounding remainder (1.000.000 / 3). */
    @Test
    fun `given 1000000 over 3 months when portions are billed then remainder goes to the last one (BR-035)`() {
        val account =
            CreditFixtures.account(
                entries = listOf(CreditFixtures.expense(LocalDate.of(2026, 11, 22), 1_000_000L)),
                instalments = listOf(CreditFixtures.plan(LocalDate.of(2026, 11, 22), 1_000_000L, 3, november)),
            )

        assertThat(StatementCalculator.unbilledInstalments(account, november)).isEqualTo(Money(666_667L))
        assertThat(StatementCalculator.unbilledInstalments(account, december)).isEqualTo(Money(333_334L))
        assertThat(StatementCalculator.unbilledInstalments(account, YearMonth.of(2027, 1))).isEqualTo(Money(0L))
    }

    /** BR-032: a December statement with statement day 28 and due day 5 is due in January (year boundary). */
    @Test
    fun `given statement day 28 and due day 5 when December then statement is due 5 Jan 2027 (BR-032)`() {
        val account = CreditFixtures.account().copy(terms = CreditTerms(Money(10_000_000L), 28, 5))

        val summary = StatementCalculator.summary(account, december, LocalDate.of(2026, 12, 29))

        assertThat(summary.period.dueDate).isEqualTo(LocalDate.of(2027, 1, 5))
    }

    /** BR-034: on 3 Jan 2027 the December statement is the current one (year boundary). */
    @Test
    fun `given 3 Jan 2027 when current statement is read then it is the December statement (BR-034)`() {
        val account = CreditFixtures.workedExample()

        val current = StatementCalculator.current(account, LocalDate.of(2027, 1, 3))

        assertThat(current.period.month).isEqualTo(december)
        assertThat(current.status).isEqualTo(StatementStatus.DUE)
    }

    /** BR-034: on 24 Nov the November statement is not yet issued, so the October statement is current. */
    @Test
    fun `given 24 Nov when current statement is read then it is the October statement (BR-034)`() {
        val current = StatementCalculator.current(CreditFixtures.workedExample(), LocalDate.of(2026, 11, 24))

        assertThat(current.period.month).isEqualTo(YearMonth.of(2026, 10))
    }
}

private fun CreditAccount.withEntries(vararg extra: CreditEntry): CreditAccount = copy(entries = entries + extra)

private fun CreditAccount.withoutPayments(): CreditAccount = copy(entries = entries.filterNot { it.isTransferIn })
