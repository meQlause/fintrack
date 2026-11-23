package com.meqlause.fintrack.core.domain.budget

import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.model.Budget
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionType
import java.time.ZoneId

/** BR-040, FR-070, FR-074: what a budget has spent in one period. */
public object BudgetSpending {
    /**
     * BR-040, FR-074: sums EXPENSE transactions dated inside [period] (in [zone]) for the budget's category;
     * a null categoryId counts every expense. Transfers and loan movements never count.
     */
    public fun spentIn(
        budget: Budget,
        transactions: List<Transaction>,
        period: Period,
        zone: ZoneId,
    ): Money {
        val total =
            transactions
                .filter { it.movement.type == TransactionType.EXPENSE }
                .filter { budget.categoryId == null || it.routing.categoryId == budget.categoryId }
                .filter { isInside(it, period, zone) }
                .sumOf { it.movement.amount.rupiah }
        return Money(total)
    }

    private fun isInside(
        transaction: Transaction,
        period: Period,
        zone: ZoneId,
    ): Boolean {
        val date =
            transaction.timing.occurredAt
                .atZone(zone)
                .toLocalDate()
        return !date.isBefore(period.startDate) && !date.isAfter(period.endDate)
    }
}
