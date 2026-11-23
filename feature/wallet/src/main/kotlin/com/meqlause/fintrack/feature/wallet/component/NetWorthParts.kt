package com.meqlause.fintrack.feature.wallet.component

import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.wallet.R

// BR-053, S-09: the five parts of the net-worth card in bar order, with the compact totals of the prototype
internal class NetWorthParts private constructor(
    val assets: List<NetWorthPart>,
    val debts: List<NetWorthPart>,
) {
    val every: List<NetWorthPart> = assets + debts
    val assetTotal: String = "Rp" + MoneyFormatter.formatCompact(Money(positiveSum(assets)))
    val debtTotal: String = "−" + MoneyFormatter.formatCompact(Money(positiveSum(debts)))

    companion object {
        fun of(netWorth: NetWorth): NetWorthParts =
            NetWorthParts(
                assets =
                    listOf(
                        NetWorthPart(R.string.wallet_networth_saldo, netWorth.saldo.rupiah, "teal"),
                        NetWorthPart(R.string.wallet_networth_invest, netWorth.investasi.rupiah, "green"),
                        NetWorthPart(R.string.wallet_networth_receivable, netWorth.piutang.rupiah, "cyan"),
                    ),
                debts =
                    listOf(
                        NetWorthPart(R.string.wallet_networth_card_debt, netWorth.utangKartu.rupiah, "rose"),
                        NetWorthPart(R.string.wallet_networth_debt, netWorth.hutang.rupiah, "orange"),
                    ),
            )

        private fun positiveSum(parts: List<NetWorthPart>): Long = parts.sumOf { it.amount.coerceAtLeast(0L) }
    }
}
