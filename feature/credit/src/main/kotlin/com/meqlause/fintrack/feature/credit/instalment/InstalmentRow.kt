package com.meqlause.fintrack.feature.credit.instalment

import com.meqlause.fintrack.core.model.Money
import java.time.YearMonth

// FR-142, FR-144, S-16: one instalment plan as the list, the S-14 section and the detail show it
internal data class InstalmentRow(
    val planId: Long,
    val title: String,
    val cardName: String,
    val tenor: Int,
    val billed: Int,
    val monthly: Money,
    val remaining: Money,
    val nextMonth: YearMonth?,
    val payoffMonth: YearMonth?,
    val portions: List<InstalmentPortionRow>,
) {
    // FR-142: a plan is finished once no principal is left to bill
    val isFinished: Boolean
        get() = remaining.isZero

    // FR-144: early payoff is offered while portions are unbilled and no payoff statement is set yet
    val canPayOff: Boolean
        get() = !isFinished && payoffMonth == null
}
