package com.meqlause.fintrack.feature.credit.instalment

import com.meqlause.fintrack.core.domain.credit.CreditAccountFactory
import com.meqlause.fintrack.core.domain.credit.InstallmentScheduler
import com.meqlause.fintrack.core.domain.credit.StatementDates
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.Transaction
import java.time.LocalDate
import java.time.ZoneId

// FR-142, FR-144, BR-035 to BR-037: the view of a plan, billed up to the latest statement cut-off on [today]
internal fun InstallmentPlan.toRow(
    cardName: String,
    transactions: List<Transaction>,
    statementDay: Int,
    today: LocalDate,
    zone: ZoneId,
): InstalmentRow? {
    val instalment = CreditAccountFactory.instalmentOf(this, transactions, zone) ?: return null
    val current = StatementDates.latestCutOffMonth(today, statementDay)
    val scheduled = InstallmentScheduler.schedule(instalment)
    val portions =
        scheduled.map { portion ->
            val billed = !portion.statementMonth.isAfter(current)
            InstalmentPortionRow(portion.number, portion.statementMonth, portion.amount, billed)
        }
    return InstalmentRow(
        planId = installmentPlanId.value,
        title = purchase.title,
        cardName = cardName,
        tenor = instalment.tenorMonths,
        billed = portions.count { it.billed },
        monthly = InstallmentScheduler.portionAmount(instalment, 1),
        remaining = InstallmentScheduler.remainingPrincipal(instalment, current),
        nextMonth = portions.firstOrNull { !it.billed }?.statementMonth,
        payoffMonth = schedule.earlyPayoffMonth,
        portions = portions,
    )
}
