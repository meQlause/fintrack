package com.meqlause.fintrack.feature.investment

import com.meqlause.fintrack.core.domain.investment.InvestmentValuation
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.ui.format.AmountSign
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import kotlin.math.abs

// FR-170, FR-173: the Indonesian labels of investment kinds and the gain line
internal object InvestmentLabels {
    // ADR-017: the label of each investment kind
    fun kindRes(kind: InvestKind): Int =
        when (kind) {
            InvestKind.MUTUAL_FUND -> R.string.investment_kind_mutual_fund
            InvestKind.STOCK -> R.string.investment_kind_stock
            InvestKind.GOLD -> R.string.investment_kind_gold
            InvestKind.DEPOSIT -> R.string.investment_kind_deposit
            InvestKind.CRYPTO -> R.string.investment_kind_crypto
            InvestKind.OTHER -> R.string.investment_kind_other
        }

    // FR-173: "+Rp80.000 (+4,0%)" for a gain, with the percentage in tenths of a percent
    fun gainLine(valuation: InvestmentValuation): String {
        val sign = if (valuation.gainTenthPercent < 0L) "" else "+"
        val money = MoneyFormatter.formatSigned(valuation.gain, AmountSign.PLUS)
        return "$money ($sign${percentText(valuation.gainTenthPercent)}%)"
    }

    // FR-173: a percentage with one decimal and a decimal comma, e.g. 40 tenths reads as 4,0
    fun percentText(tenths: Long): String {
        val magnitude = abs(tenths)
        val sign = if (tenths < 0L) "-" else ""
        return "$sign${magnitude / TENTHS},${magnitude % TENTHS}"
    }

    private const val TENTHS = 10L
}
