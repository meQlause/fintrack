package com.meqlause.fintrack.core.domain.investment

import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.ValuationEntry
import com.meqlause.fintrack.core.model.WalletId

// FR-173: the timeline of one investment wallet, transfers and valuations, newest first
internal object InvestmentActivityBuilder {
    fun activityOf(
        walletId: WalletId,
        transactions: List<Transaction>,
        valuations: List<ValuationEntry>,
    ): List<InvestmentActivity> {
        val transfers = transactions.mapNotNull { transfer(it, walletId) }
        val valued =
            valuations.filter { it.walletId == walletId }.map {
                InvestmentActivity(walletId, it.valuedAt, ActivityKind.VALUATION, it.value)
            }
        return (transfers + valued).sortedByDescending { it.occurredAt }
    }

    private fun transfer(
        transaction: Transaction,
        walletId: WalletId,
    ): InvestmentActivity? {
        val flow = InvestmentAccountBuilder.flowOf(transaction, walletId) ?: return null
        val kind = if (flow.direction == MoneyDirection.CREDIT) ActivityKind.PUT_IN else ActivityKind.TAKE_OUT
        return InvestmentActivity(walletId, flow.occurredAt, kind, flow.amount)
    }
}
