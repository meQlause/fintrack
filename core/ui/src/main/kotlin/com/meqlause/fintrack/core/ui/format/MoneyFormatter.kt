package com.meqlause.fintrack.core.ui.format

import com.meqlause.fintrack.core.model.Money

// DOC-08 / NFR-011: Rp1.250.000 in full, −Rp25.000 for negatives, 4,5rb / 25rb / 1,3jt when compact
object MoneyFormatter {
    private const val CURRENCY_PREFIX = "Rp"
    private const val MINUS_SIGN = "−"
    private const val PLUS_SIGN = "+"
    private const val THOUSAND = 1_000L
    private const val TEN_THOUSAND = 10_000L
    private const val MILLION = 1_000_000L
    private const val HUNDRED = 100L
    private const val TENTHS_PER_UNIT = 10L
    private const val HUNDRED_THOUSAND = 100_000L
    private const val GROUP_SIZE = 3
    private const val GROUP_SEPARATOR = "."
    private const val DECIMAL_SEPARATOR = ","

    /** DOC-08: whole rupiah with a Rp prefix and dot grouping; a negative amount starts with U+2212. */
    fun format(money: Money): String = formatSigned(money, AmountSign.NONE)

    /** DOC-08: the amount with the prefix of [sign]; a negative amount always shows U+2212. */
    fun formatSigned(
        money: Money,
        sign: AmountSign,
    ): String {
        val prefix =
            when {
                money.rupiah < 0L -> MINUS_SIGN
                sign == AmountSign.MINUS -> MINUS_SIGN
                sign == AmountSign.PLUS -> PLUS_SIGN
                else -> ""
            }
        return prefix + CURRENCY_PREFIX + groupThousands(magnitude(money.rupiah))
    }

    /** DOC-08: compact form for charts: rb below 1 jt, jt from 1 jt; rounded half up. */
    fun formatCompact(money: Money): String {
        val magnitude = magnitude(money.rupiah)
        val body =
            when {
                magnitude >= MILLION -> oneDecimal(roundedDivide(magnitude, HUNDRED_THOUSAND), "jt")
                magnitude >= TEN_THOUSAND -> "${roundedDivide(magnitude, THOUSAND)}rb"
                magnitude >= THOUSAND -> oneDecimal(roundedDivide(magnitude, HUNDRED), "rb")
                else -> magnitude.toString()
            }
        return if (money.rupiah < 0L) MINUS_SIGN + body else body
    }

    private fun magnitude(rupiah: Long): Long = if (rupiah < 0L) -rupiah else rupiah

    private fun roundedDivide(
        value: Long,
        divisor: Long,
    ): Long = (value + divisor / 2) / divisor

    // tenths is the amount in units of one tenth of the suffix unit, so 1,3jt is tenths 13
    private fun oneDecimal(
        tenths: Long,
        suffix: String,
    ): String {
        val whole = tenths / TENTHS_PER_UNIT
        val fraction = tenths % TENTHS_PER_UNIT
        return if (fraction == 0L) "$whole$suffix" else "$whole$DECIMAL_SEPARATOR$fraction$suffix"
    }

    private fun groupThousands(digits: Long): String =
        digits
            .toString()
            .reversed()
            .chunked(GROUP_SIZE)
            .joinToString(GROUP_SEPARATOR)
            .reversed()
}
