package com.meqlause.fintrack.core.domain.investment

import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Money

/** FR-173, FR-174: every active investment account, the totals, the allocation by kind and the newest activity. */
public data class InvestmentPortfolio(
    public val accounts: List<InvestmentAccount>,
    public val recent: List<InvestmentActivity>,
) {
    /** FR-173: total modal and total value over all accounts, without a last valuation date. */
    public val total: InvestmentValuation
        get() =
            InvestmentValuation(
                modal = accounts.fold(Money(0L)) { sum, account -> sum + account.valuation.modal },
                currentValue = accounts.fold(Money(0L)) { sum, account -> sum + account.valuation.currentValue },
                lastValuedAt = null,
            )

    /** FR-173: current value per investment kind, largest first. */
    public val allocation: Map<InvestKind, Money>
        get() =
            accounts
                .groupBy { it.wallet.kind.investKind ?: InvestKind.OTHER }
                .mapValues { (_, group) -> group.fold(Money(0L)) { sum, item -> sum + item.valuation.currentValue } }
                .toList()
                .sortedByDescending { it.second.rupiah }
                .toMap()
}
