package com.meqlause.fintrack.core.domain.loan

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

// FR-158: the due-date choices offered when a loan is recorded or its due date is changed later

/** FR-158: loan due option tests. */
class LoanDueOptionTest {
    private val start = LocalDate.of(2026, 11, 30)

    /** FR-158: choosing no due date gives no date at all. */
    @Test
    fun givenNoneWhenDueFromThenDateIsNull() {
        assertThat(LoanDueOption.NONE.dueFrom(start)).isNull()
    }

    /** FR-158: week options count days from the start date. */
    @Test
    fun givenWeekOptionsWhenDueFromThenWeeksAreAdded() {
        assertThat(LoanDueOption.ONE_WEEK.dueFrom(start)).isEqualTo(LocalDate.of(2026, 12, 7))
        assertThat(LoanDueOption.TWO_WEEKS.dueFrom(start)).isEqualTo(LocalDate.of(2026, 12, 14))
    }

    /** FR-158: month options add whole months and keep the last day of a shorter month. */
    @Test
    fun givenMonthOptionsWhenDueFromThenMonthsAreAddedAndClamped() {
        assertThat(LoanDueOption.ONE_MONTH.dueFrom(start)).isEqualTo(LocalDate.of(2026, 12, 30))
        assertThat(LoanDueOption.TWO_MONTHS.dueFrom(start)).isEqualTo(LocalDate.of(2027, 1, 30))
        assertThat(LoanDueOption.THREE_MONTHS.dueFrom(start)).isEqualTo(LocalDate.of(2027, 2, 28))
    }
}
