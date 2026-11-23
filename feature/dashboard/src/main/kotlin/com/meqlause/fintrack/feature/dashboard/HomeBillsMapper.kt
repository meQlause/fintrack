package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.domain.bills.BillsCard
import com.meqlause.fintrack.core.domain.bills.CreditBill
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.StatementStatus
import com.meqlause.fintrack.core.ui.format.DateFormatter
import java.time.Clock

// FR-137, FR-146, S-03: turns the domain bills card into the rows Beranda renders; pure apart from the clock
internal object HomeBillsMapper {
    // FR-137: the amount is what is still due, or the whole statement once it is paid
    fun map(
        card: BillsCard,
        clock: Clock,
    ): HomeBills =
        HomeBills(
            bills = card.bills.map { bill -> billOf(bill, clock) },
            instalmentsDue = card.instalmentsDue,
        )

    private fun billOf(
        bill: CreditBill,
        clock: Clock,
    ): HomeBill {
        val statement = bill.statement
        val date =
            when (statement.status) {
                StatementStatus.NOT_ISSUED -> statement.period.cutOff
                StatementStatus.PAID -> statement.period.cutOff.plusMonths(1L)
                else -> statement.period.dueDate
            }
        return HomeBill(
            walletId = bill.walletId,
            name = bill.walletName,
            status = statement.status,
            amount = amountOf(bill),
            dateLabel = DateFormatter.dayLabel(date, clock),
        )
    }

    private fun amountOf(bill: CreditBill): Money {
        val statement = bill.statement
        return when {
            statement.status == StatementStatus.NOT_ISSUED -> Money(0L)
            statement.breakdown.remainingDue.isZero -> statement.breakdown.statementAmount
            else -> statement.breakdown.remainingDue
        }
    }
}
