package com.meqlause.fintrack.core.domain.reminder

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

// BR-045: reminders fire only on D-3 and D-1 of a due date
class ReminderRulesTest {
    private val today = LocalDate.parse("2026-11-20")

    @Test
    fun givenDueInThreeDaysWhenLeadOfThenThree() {
        assertThat(ReminderRules.leadOf(LocalDate.parse("2026-11-23"), today)).isEqualTo(3)
    }

    @Test
    fun givenDueTomorrowWhenLeadOfThenOne() {
        assertThat(ReminderRules.leadOf(LocalDate.parse("2026-11-21"), today)).isEqualTo(1)
    }

    @Test
    fun givenDueInTwoDaysOrDueTodayWhenLeadOfThenNull() {
        assertThat(ReminderRules.leadOf(LocalDate.parse("2026-11-22"), today)).isNull()
        assertThat(ReminderRules.leadOf(today, today)).isNull()
    }

    @Test
    fun givenPastDueDateWhenLeadOfThenNull() {
        assertThat(ReminderRules.leadOf(LocalDate.parse("2026-11-18"), today)).isNull()
    }
}
