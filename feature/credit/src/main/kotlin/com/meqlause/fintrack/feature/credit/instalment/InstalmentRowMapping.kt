package com.meqlause.fintrack.feature.credit.instalment

import com.meqlause.fintrack.core.domain.credit.CreditAccountFactory
import com.meqlause.fintrack.core.domain.credit.CreditInstalment
import com.meqlause.fintrack.core.domain.credit.InstallmentScheduler
import com.meqlause.fintrack.core.domain.credit.StatementDates
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.Transaction
import java.time.YearMonth
import java.time.ZoneId

// FR-142, FR-144, BR-035 to BR-037: the view of a plan, billed up to the latest statement cut-off on [today]
internal fun InstallmentPlan.toRow(
    cardName: String,
    transactions: List<Transaction>,
    cutOff: StatementCutOff,
    zone: ZoneId,
): InstalmentRow? {
    val (statementDay, today) = cutOff
    val instalment = CreditAccountFactory.instalmentOf(this, transactions, zone) ?: return null
    val current = StatementDates.latestCutOffMonth(today, statementDay)
    val portions = portionRows(instalment, current)
    val progress =
        InstalmentProgress(
            tenor = instalment.tenorMonths,
            billed = portions.count { it.billed },
            nextMonth = portions.firstOrNull { !it.billed }?.statementMonth,
            payoffMonth = schedule.earlyPayoffMonth,
        )
    val amounts =
        InstalmentAmounts(
            monthly = InstallmentScheduler.portionAmount(instalment, 1),
            remaining = InstallmentScheduler.remainingPrincipal(instalment, current),
        )
    val info = InstalmentPlanInfo(planId = installmentPlanId.value, title = purchase.title, cardName = cardName)
    return InstalmentRow(plan = info, progress = progress, amounts = amounts, portions = portions)
}

// FR-142: each portion of the schedule, billed once its statement month is not after the current one
private fun portionRows(
    instalment: CreditInstalment,
    current: YearMonth,
): List<InstalmentPortionRow> =
    InstallmentScheduler.schedule(instalment).map { portion ->
        InstalmentPortionRow(
            portion.number,
            portion.statementMonth,
            portion.amount,
            !portion.statementMonth.isAfter(current),
        )
    }
