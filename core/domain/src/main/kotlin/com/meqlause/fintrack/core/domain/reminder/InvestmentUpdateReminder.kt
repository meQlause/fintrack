package com.meqlause.fintrack.core.domain.reminder

import java.time.YearMonth

/** FR-176, BR-055: the optional monthly "update nilai investasi" reminder */
public data class InvestmentUpdateReminder(
    public val month: YearMonth,
) : Reminder {
    override val reminderKey: String
        get() = "invest-$month"
}
