package com.meqlause.fintrack.core.domain.credit

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.YearMonth

/** BR-032, BR-036: statement cut-off, due date, period and first statement month. */
class StatementDatesTest {
    /** BR-032: the cut-off is the statement day of the statement month. */
    @Test
    fun `given statement day 25 when month is November 2026 then cut-off is 25 Nov 2026 (BR-032)`() {
        assertThat(StatementDates.cutOff(YearMonth.of(2026, 11), 25)).isEqualTo(LocalDate.of(2026, 11, 25))
    }

    /** BR-032: the period covers the day after the previous cut-off up to this cut-off. */
    @Test
    fun `given statement day 25 when month is November 2026 then period starts 26 Oct 2026 (BR-032)`() {
        assertThat(StatementDates.periodStart(YearMonth.of(2026, 11), 25)).isEqualTo(LocalDate.of(2026, 10, 26))
    }

    /** BR-032: due day greater than statement day falls in the cut-off month. */
    @Test
    fun `given statement day 5 and due day 20 when November then due is 20 Nov 2026 (BR-032)`() {
        assertThat(StatementDates.dueDate(YearMonth.of(2026, 11), 5, 20)).isEqualTo(LocalDate.of(2026, 11, 20))
    }

    /** BR-032: due day smaller than statement day falls in the following month. */
    @Test
    fun `given statement day 25 and due day 10 when November then due is 10 Dec 2026 (BR-032)`() {
        assertThat(StatementDates.dueDate(YearMonth.of(2026, 11), 25, 10)).isEqualTo(LocalDate.of(2026, 12, 10))
    }

    /** BR-032: due day equal to statement day is not greater, so it falls in the following month. */
    @Test
    fun `given due day equal to statement day when November then due is in December (BR-032)`() {
        assertThat(StatementDates.dueDate(YearMonth.of(2026, 11), 25, 25)).isEqualTo(LocalDate.of(2026, 12, 25))
    }

    /** BR-032: a December statement with an earlier due day is due in January of the next year. */
    @Test
    fun `given statement day 25 and due day 10 when December 2026 then due is 10 Jan 2027 (BR-032)`() {
        assertThat(StatementDates.dueDate(YearMonth.of(2026, 12), 25, 10)).isEqualTo(LocalDate.of(2027, 1, 10))
    }

    /** BR-032: year boundary with the due day in the same month as the cut-off is not rolled over. */
    @Test
    fun `given statement day 1 and due day 28 when December 2026 then due is 28 Dec 2026 (BR-032)`() {
        assertThat(StatementDates.dueDate(YearMonth.of(2026, 12), 1, 28)).isEqualTo(LocalDate.of(2026, 12, 28))
    }

    /** BR-032: a January period starts in December of the previous year. */
    @Test
    fun `given statement day 25 when January 2027 then period starts 26 Dec 2026 (BR-032)`() {
        assertThat(StatementDates.periodStart(YearMonth.of(2027, 1), 25)).isEqualTo(LocalDate.of(2026, 12, 26))
    }

    /** BR-036: the first statement is the one whose cut-off is on or after the purchase date. */
    @Test
    fun `given purchase before cut-off when first statement is chosen then it is the same month (BR-036)`() {
        val first = StatementDates.firstStatementMonth(LocalDate.of(2026, 11, 22), 25)

        assertThat(first).isEqualTo(YearMonth.of(2026, 11))
    }

    /** BR-036: a purchase on the cut-off day is still billed on that statement. */
    @Test
    fun `given purchase on cut-off day when first statement is chosen then it is the same month (BR-036)`() {
        val first = StatementDates.firstStatementMonth(LocalDate.of(2026, 11, 25), 25)

        assertThat(first).isEqualTo(YearMonth.of(2026, 11))
    }

    /** BR-036: a purchase after the cut-off is billed on the next statement. */
    @Test
    fun `given purchase after cut-off when first statement is chosen then it is the next month (BR-036)`() {
        val first = StatementDates.firstStatementMonth(LocalDate.of(2026, 11, 26), 25)

        assertThat(first).isEqualTo(YearMonth.of(2026, 12))
    }

    /** BR-034: the latest cut-off on or before today is the latest issued statement. */
    @Test
    fun `given today before cut-off when latest cut-off is chosen then it is the previous month (BR-034)`() {
        val latest = StatementDates.latestCutOffMonth(LocalDate.of(2026, 11, 24), 25)

        assertThat(latest).isEqualTo(YearMonth.of(2026, 10))
    }

    /** BR-034: today equal to the cut-off issues that statement. */
    @Test
    fun `given today on cut-off day when latest cut-off is chosen then it is the same month (BR-034)`() {
        val latest = StatementDates.latestCutOffMonth(LocalDate.of(2026, 11, 25), 25)

        assertThat(latest).isEqualTo(YearMonth.of(2026, 11))
    }

    /** BR-034: after New Year the latest cut-off is still the December statement. */
    @Test
    fun `given 3 Jan 2027 when latest cut-off is chosen then it is December 2026 (BR-034)`() {
        val latest = StatementDates.latestCutOffMonth(LocalDate.of(2027, 1, 3), 25)

        assertThat(latest).isEqualTo(YearMonth.of(2026, 12))
    }

    /** BR-032: statement days are 1 to 28 (FR-130). */
    @Test
    fun `given statement day 29 when cut-off is computed then it is rejected (BR-032)`() {
        assertThrows<IllegalArgumentException> { StatementDates.cutOff(YearMonth.of(2026, 11), 29) }
    }
}
