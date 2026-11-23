package com.meqlause.fintrack.core.domain.reminder

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** BR-045: lead times and run hour of the reminder job */
public object ReminderRules {
    /** BR-045: the job runs at 09:00 local time. */
    public const val RUN_HOUR: Int = 9
    private const val FAR_LEAD_DAYS = 3
    private const val NEAR_LEAD_DAYS = 1
    private val leadDays = setOf(FAR_LEAD_DAYS, NEAR_LEAD_DAYS)

    /** BR-045: the lead time in days when [today] is D-3 or D-1 of [dueDate]; null on every other day. */
    public fun leadOf(
        dueDate: LocalDate,
        today: LocalDate,
    ): Int? =
        ChronoUnit.DAYS
            .between(today, dueDate)
            .toInt()
            .takeIf { it in leadDays }
}
