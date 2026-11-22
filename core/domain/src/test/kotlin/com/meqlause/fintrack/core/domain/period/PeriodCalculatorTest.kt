package com.meqlause.fintrack.core.domain.period

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

/** BR-010, FR-050: the period containing a date for a configurable start day. */
class PeriodCalculatorTest {
    /** BR-010: start day 1 gives calendar months. */
    @Test
    fun `given start day 1 when date is 30 November then period is 1 Nov to 30 Nov (BR-010)`() {
        val period = PeriodCalculator.periodContaining(LocalDate.of(2026, 11, 30), 1)

        assertThat(period).isEqualTo(Period(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30)))
    }

    /** BR-010, FR-050: the example from FR-050. */
    @Test
    fun `given start day 25 when date is 24 November then period is 25 Oct to 24 Nov (BR-010 FR-050)`() {
        val period = PeriodCalculator.periodContaining(LocalDate.of(2026, 11, 24), 25)

        assertThat(period).isEqualTo(Period(LocalDate.of(2026, 10, 25), LocalDate.of(2026, 11, 24)))
    }

    /** BR-010: a date before the start day belongs to the period that began last month. */
    @Test
    fun `given start day 25 when date is 24 March then period is 25 Feb to 24 Mar (BR-010)`() {
        val period = PeriodCalculator.periodContaining(LocalDate.of(2026, 3, 24), 25)

        assertThat(period).isEqualTo(Period(LocalDate.of(2026, 2, 25), LocalDate.of(2026, 3, 24)))
    }

    /** BR-010: a date equal to the start day begins a new period. */
    @Test
    fun `given start day 25 when date is 25 March then period is 25 Mar to 24 Apr (BR-010)`() {
        val period = PeriodCalculator.periodContaining(LocalDate.of(2026, 3, 25), 25)

        assertThat(period).isEqualTo(Period(LocalDate.of(2026, 3, 25), LocalDate.of(2026, 4, 24)))
    }

    /** BR-010: start day 28 with the last day before it in February. */
    @Test
    fun `given start day 28 when date is 27 February 2027 then period is 28 Jan to 27 Feb (BR-010)`() {
        val period = PeriodCalculator.periodContaining(LocalDate.of(2027, 2, 27), 28)

        assertThat(period).isEqualTo(Period(LocalDate.of(2027, 1, 28), LocalDate.of(2027, 2, 27)))
    }

    /** BR-010: start day 28 on the 28th of a non-leap February. */
    @Test
    fun `given start day 28 when date is 28 February 2027 then period is 28 Feb to 27 Mar (BR-010)`() {
        val period = PeriodCalculator.periodContaining(LocalDate.of(2027, 2, 28), 28)

        assertThat(period).isEqualTo(Period(LocalDate.of(2027, 2, 28), LocalDate.of(2027, 3, 27)))
    }

    /** BR-010: leap year 2028, start day 28 on 28 February. */
    @Test
    fun `given start day 28 when date is 28 February 2028 then period is 28 Feb to 27 Mar (BR-010)`() {
        val period = PeriodCalculator.periodContaining(LocalDate.of(2028, 2, 28), 28)

        assertThat(period).isEqualTo(Period(LocalDate.of(2028, 2, 28), LocalDate.of(2028, 3, 27)))
    }

    /** BR-010: leap year 2028, start day 1 ends on 29 February. */
    @Test
    fun `given start day 1 when date is 29 February 2028 then period is 1 Feb to 29 Feb (BR-010)`() {
        val period = PeriodCalculator.periodContaining(LocalDate.of(2028, 2, 29), 1)

        assertThat(period).isEqualTo(Period(LocalDate.of(2028, 2, 1), LocalDate.of(2028, 2, 29)))
    }

    /** BR-010: a January date with a start day in December crosses the year boundary. */
    @Test
    fun `given start day 25 when date is 3 January 2027 then period is 25 Dec 2026 to 24 Jan 2027 (BR-010)`() {
        val period = PeriodCalculator.periodContaining(LocalDate.of(2027, 1, 3), 25)

        assertThat(period).isEqualTo(Period(LocalDate.of(2026, 12, 25), LocalDate.of(2027, 1, 24)))
    }

    /** BR-010: the last day of December with start day 1 stays in December. */
    @Test
    fun `given start day 1 when date is 31 December 2026 then period is 1 Dec to 31 Dec (BR-010)`() {
        val period = PeriodCalculator.periodContaining(LocalDate.of(2026, 12, 31), 1)

        assertThat(period).isEqualTo(Period(LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 31)))
    }

    /** BR-010: the next period starts the day after the current one ends. */
    @Test
    fun `given period 25 Oct to 24 Nov when next period is asked then it is 25 Nov to 24 Dec (BR-010)`() {
        val current = Period(LocalDate.of(2026, 10, 25), LocalDate.of(2026, 11, 24))

        val next = PeriodCalculator.nextPeriod(current, 25)

        assertThat(next).isEqualTo(Period(LocalDate.of(2026, 11, 25), LocalDate.of(2026, 12, 24)))
    }

    /** BR-010, FR-062: the previous period is the one used for comparison. */
    @Test
    fun `given period 25 Oct to 24 Nov when previous period is asked then it is 25 Sep to 24 Oct (BR-010)`() {
        val current = Period(LocalDate.of(2026, 10, 25), LocalDate.of(2026, 11, 24))

        val previous = PeriodCalculator.previousPeriod(current, 25)

        assertThat(previous).isEqualTo(Period(LocalDate.of(2026, 9, 25), LocalDate.of(2026, 10, 24)))
    }

    /** FR-050: the start day is limited to 1 to 28 so every month has that day. */
    @Test
    fun `given start day 29 when period is requested then it is rejected (FR-050)`() {
        assertThrows<IllegalArgumentException> {
            PeriodCalculator.periodContaining(LocalDate.of(2026, 11, 30), 29)
        }
    }

    /** FR-050: zero is not a valid start day. */
    @Test
    fun `given start day 0 when period is requested then it is rejected (FR-050)`() {
        assertThrows<IllegalArgumentException> {
            PeriodCalculator.periodContaining(LocalDate.of(2026, 11, 30), 0)
        }
    }
}
