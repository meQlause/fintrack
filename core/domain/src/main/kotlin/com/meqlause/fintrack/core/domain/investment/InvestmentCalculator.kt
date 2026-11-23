package com.meqlause.fintrack.core.domain.investment

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.ValuationEntry

/** BR-051: values one investment wallet from its opening balance, its transfers and its manual valuations. */
public object InvestmentCalculator {
    /**
     * BR-051: modal counts every transfer; current value starts from the latest valuation and adds the
     * transfers after it.
     */
    public fun valuate(
        initialBalance: Money,
        flows: List<InvestmentFlow>,
        valuations: List<ValuationEntry>,
    ): InvestmentValuation {
        val modal = flows.fold(initialBalance) { balance, flow -> applied(balance, flow) }
        val latest = valuations.maxByOrNull { it.valuedAt } ?: return InvestmentValuation(modal, modal, null)
        val afterLatest = flows.filter { it.occurredAt.isAfter(latest.valuedAt) }
        val current = afterLatest.fold(latest.value) { balance, flow -> applied(balance, flow) }
        return InvestmentValuation(modal = modal, currentValue = current, lastValuedAt = latest.valuedAt)
    }

    // BR-051: money put in adds its amount, money taken out subtracts it
    private fun applied(
        balance: Money,
        flow: InvestmentFlow,
    ): Money =
        when (flow.direction) {
            MoneyDirection.CREDIT -> balance + flow.amount
            MoneyDirection.DEBIT -> balance - flow.amount
        }
}
