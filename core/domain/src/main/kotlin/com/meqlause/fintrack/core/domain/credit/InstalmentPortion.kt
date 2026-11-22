package com.meqlause.fintrack.core.domain.credit

import com.meqlause.fintrack.core.model.Money
import java.time.YearMonth

/** BR-035, BR-036: one computed portion of a plan and the statement month that bills it. */
public data class InstalmentPortion(
    public val number: Int,
    public val statementMonth: YearMonth,
    public val amount: Money,
)
