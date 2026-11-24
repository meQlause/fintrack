package com.meqlause.fintrack.feature.wallet.component

import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.wallet.R

// BR-053, S-09: the five parts of the net-worth card in bar order; Aset counts a negative Saldo
internal class NetWorthParts private constructor(
    val assets: List<NetWorthPart>,
    val debts: List<NetWorthPart>,
    val assetTotal: String,
    val debtTotal: String,
) {
    val every: List<NetWorthPart> = assets + debts

    companion object {
        fun from(netWorth: NetWorth): NetWorthParts =
            NetWorthParts(
                assetTotal = MoneyFormatter.formatShort(netWorth.assets),
                debtTotal = MoneyFormatter.formatShort(Money(-netWorth.debts.rupiah)),
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
    }
}
