package com.meqlause.fintrack.core.domain.reminder

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

// BR-045: reminders fire only on D-3 and D-1 of a due date

/** Reminder rules tests. */
class ReminderRulesTest {
    private val today = LocalDate.parse("2026-11-20")

    /** Given due in three days when lead of then three. */
    @Test
    fun givenDueInThreeDaysWhenLeadOfThenThree() {
        assertThat(ReminderRules.leadOf(LocalDate.parse("2026-11-23"), today)).isEqualTo(3)
    }

    /** Given due tomorrow when lead of then one. */
    @Test
    fun givenDueTomorrowWhenLeadOfThenOne() {
        assertThat(ReminderRules.leadOf(LocalDate.parse("2026-11-21"), today)).isEqualTo(1)
    }

    /** Given due in two days or due today when lead of then null. */
    @Test
    fun givenDueInTwoDaysOrDueTodayWhenLeadOfThenNull() {
        assertThat(ReminderRules.leadOf(LocalDate.parse("2026-11-22"), today)).isNull()
        assertThat(ReminderRules.leadOf(today, today)).isNull()
    }

    /** Given past due date when lead of then null. */
    @Test
    fun givenPastDueDateWhenLeadOfThenNull() {
        assertThat(ReminderRules.leadOf(LocalDate.parse("2026-11-18"), today)).isNull()
    }
}
