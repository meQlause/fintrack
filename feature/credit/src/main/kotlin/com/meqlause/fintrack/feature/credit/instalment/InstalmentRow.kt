package com.meqlause.fintrack.feature.credit.instalment

import com.meqlause.fintrack.core.model.Money
import java.time.YearMonth

// FR-142, FR-144, S-16: one instalment plan as the list, the S-14 section and the detail show it
internal data class InstalmentRow(
    val plan: InstalmentPlanInfo,
    val progress: InstalmentProgress,
    val sums: InstalmentAmounts,
    val portions: List<InstalmentPortionRow>,
) {
    val planId: Long get() = plan.planId
    val title: String get() = plan.title
    val cardName: String get() = plan.cardName
    val tenor: Int get() = progress.tenor
    val billed: Int get() = progress.billed
    val nextMonth: YearMonth? get() = progress.nextMonth
    val payoffMonth: YearMonth? get() = progress.payoffMonth
    val monthly: Money get() = sums.monthly
    val remaining: Money get() = sums.remaining

    // FR-142: a plan is finished once no principal is left to bill
    val isFinished: Boolean
        get() = remaining.isZero

    // FR-144: early payoff is offered while portions are unbilled and no payoff statement is set yet
    val canPayOff: Boolean
        get() = !isFinished && payoffMonth == null
}

// FR-142: which plan a row is, and on which card
internal data class InstalmentPlanInfo(
    val planId: Long,
    val title: String,
    val cardName: String,
)

// FR-142, FR-144: the tenor, the portions billed so far, the next statement and the early payoff month
internal data class InstalmentProgress(
    val tenor: Int,
    val billed: Int,
    val nextMonth: YearMonth?,
    val payoffMonth: YearMonth?,
)

// FR-142: the monthly portion and the principal that is left
internal data class InstalmentAmounts(
    val monthly: Money,
    val remaining: Money,
)
