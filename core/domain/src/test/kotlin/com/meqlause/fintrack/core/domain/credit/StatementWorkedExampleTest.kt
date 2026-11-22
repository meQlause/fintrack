package com.meqlause.fintrack.core.domain.credit

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.StatementStatus
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.YearMonth

/** DOC-18 section 2: every line of the worked example, one assertion per line. */
class StatementWorkedExampleTest {
    private val account = CreditFixtures.workedExample()
    private val november = YearMonth.of(2026, 11)
    private val december = YearMonth.of(2026, 12)

    /** DOC-18: "20 Nov Shoes -500.000 (EXPENSE)" moves the balance to -500.000 (BR-031). */
    @Test
    fun `given 20 Nov shoes when balance is read on 20 Nov then it is minus 500000 (BR-031)`() {
        assertThat(StatementCalculator.balanceOn(account, LocalDate.of(2026, 11, 20))).isEqualTo(Money(-500_000L))
    }

    /** DOC-18: "22 Nov Phone 6x plan -3.000.000" first portion is billed on statement Nov 2026 (BR-036). */
    @Test
    fun `given 22 Nov phone plan when first statement is chosen then it is Nov 2026 (BR-036)`() {
        assertThat(StatementDates.firstStatementMonth(LocalDate.of(2026, 11, 22), 25)).isEqualTo(november)
    }

    /** DOC-18: "22 Nov Phone 6x plan -3.000.000" moves the balance to -3.500.000 (BR-031). */
    @Test
    fun `given 22 Nov phone plan when balance is read on 22 Nov then it is minus 3500000 (BR-031)`() {
        assertThat(StatementCalculator.balanceOn(account, LocalDate.of(2026, 11, 22))).isEqualTo(Money(-3_500_000L))
    }

    /** DOC-18: "25 Nov cut-off -> outstanding 3.500.000" (BR-031). */
    @Test
    fun `given 25 Nov cut-off when outstanding is read then it is 3500000 (BR-031)`() {
        assertThat(StatementCalculator.outstandingOn(account, LocalDate.of(2026, 11, 25))).isEqualTo(Money(3_500_000L))
    }

    /** DOC-18: "unbilled plan principal 2.500.000" at the Nov cut-off (BR-033, BR-036). */
    @Test
    fun `given 25 Nov cut-off when unbilled plan principal is read then it is 2500000 (BR-033, BR-036)`() {
        assertThat(StatementCalculator.unbilledInstalments(account, november)).isEqualTo(Money(2_500_000L))
    }

    /** DOC-18: "statementAmount = 3.500.000 - 2.500.000 = 1.000.000" (BR-033). */
    @Test
    fun `given 25 Nov cut-off when statement amount is read then it is 1000000 (BR-033)`() {
        assertThat(StatementCalculator.summary(account, november, LocalDate.of(2026, 11, 25)).breakdown.statementAmount)
            .isEqualTo(Money(1_000_000L))
    }

    /** DOC-18: "available limit = 10.000.000 - 3.500.000 = 6.500.000" (BR-044). */
    @Test
    fun `given 25 Nov when available limit is read then it is 6500000 (BR-044)`() {
        assertThat(StatementCalculator.availableLimit(account, LocalDate.of(2026, 11, 25)))
            .isEqualTo(Money(6_500_000L))
    }

    /** DOC-18: "25 Nov" the Nov statement is issued and due, since nothing is paid yet (BR-034). */
    @Test
    fun `given 25 Nov when Nov statement status is read then it is DUE (BR-034)`() {
        assertThat(StatementCalculator.summary(account, november, LocalDate.of(2026, 11, 25)).status)
            .isEqualTo(StatementStatus.DUE)
    }

    /** DOC-18: "Pay bill 1.000.000" on 1 Dec leaves remainingDue 0 and the statement is PAID (BR-033, BR-034). */
    @Test
    fun `given 1 Dec bill paid when Nov statement is read then remaining due is 0 and status PAID (BR-033, BR-034)`() {
        val summary = StatementCalculator.summary(account, november, LocalDate.of(2026, 12, 1))

        assertThat(summary.breakdown.remainingDue).isEqualTo(Money(0L))
        assertThat(summary.status).isEqualTo(StatementStatus.PAID)
    }

    /** DOC-18: "Pay bill 1.000.000" counts as paidSinceCutoff for the Nov statement (BR-033). */
    @Test
    fun `given 1 Dec bill paid when paid since cut-off is read then it is 1000000 (BR-033)`() {
        assertThat(StatementCalculator.summary(account, november, LocalDate.of(2026, 12, 1)).breakdown.paidSinceCutoff)
            .isEqualTo(Money(1_000_000L))
    }

    /** DOC-18: "25 Dec cut-off -> outstanding 2.500.000" (BR-031). */
    @Test
    fun `given 25 Dec cut-off when outstanding is read then it is 2500000 (BR-031)`() {
        assertThat(StatementCalculator.outstandingOn(account, LocalDate.of(2026, 12, 25))).isEqualTo(Money(2_500_000L))
    }

    /** DOC-18: "unbilled 2.000.000" at the Dec cut-off (BR-033, BR-036). */
    @Test
    fun `given 25 Dec cut-off when unbilled plan principal is read then it is 2000000 (BR-033, BR-036)`() {
        assertThat(StatementCalculator.unbilledInstalments(account, december)).isEqualTo(Money(2_000_000L))
    }

    /** DOC-18: "statementAmount 500.000" for the Dec statement (BR-033). */
    @Test
    fun `given 25 Dec cut-off when statement amount is read then it is 500000 (BR-033)`() {
        assertThat(StatementCalculator.summary(account, december, LocalDate.of(2026, 12, 25)).breakdown.statementAmount)
            .isEqualTo(Money(500_000L))
    }

    /** DOC-18: the Dec statement covers 26 Nov to 25 Dec and is due 10 Jan 2027 (BR-032). */
    @Test
    fun `given Dec 2026 statement when read then it covers 26 Nov to 25 Dec and is due 10 Jan 2027 (BR-032)`() {
        val period = StatementCalculator.summary(account, december, LocalDate.of(2026, 12, 25)).period

        assertThat(period.periodStart).isEqualTo(LocalDate.of(2026, 11, 26))
        assertThat(period.cutOff).isEqualTo(LocalDate.of(2026, 12, 25))
        assertThat(period.dueDate).isEqualTo(LocalDate.of(2027, 1, 10))
    }

    /** DOC-18: the Nov statement covers 26 Oct to 25 Nov and is due 10 Dec 2026 (BR-032). */
    @Test
    fun `given Nov 2026 statement when read then it covers 26 Oct to 25 Nov and is due 10 Dec 2026 (BR-032)`() {
        val period = StatementCalculator.summary(account, november, LocalDate.of(2026, 11, 25)).period

        assertThat(period.periodStart).isEqualTo(LocalDate.of(2026, 10, 26))
        assertThat(period.cutOff).isEqualTo(LocalDate.of(2026, 11, 25))
        assertThat(period.dueDate).isEqualTo(LocalDate.of(2026, 12, 10))
    }

    /** DOC-18: the Dec statement with 500.000 remaining is DUE on 25 Dec (BR-034). */
    @Test
    fun `given 25 Dec when Dec statement is read then remaining due is 500000 and status DUE (BR-034)`() {
        val summary = StatementCalculator.summary(account, december, LocalDate.of(2026, 12, 25))

        assertThat(summary.breakdown.remainingDue).isEqualTo(Money(500_000L))
        assertThat(summary.status).isEqualTo(StatementStatus.DUE)
    }
}
