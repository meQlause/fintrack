package com.meqlause.fintrack.core.parser

import com.meqlause.fintrack.core.model.Money

/** FR-083, FR-085, DOC-07: finds rupiah amounts in Indonesian or English notation, whole rupiah only (BR-001). */
public object IndonesianAmountParser {
    private val AMOUNT_PATTERN = Regex("""(?i)\b(?:Rp\.?|IDR)\s?([0-9][0-9.,]*+)(?![\p{L}0-9])""")
    private const val CAPTURE_GROUP = 1
    private const val DECIMAL_DIGITS = 2
    private const val GROUP_DIGITS = 3
    private const val MAX_FIRST_GROUP = 3

    /** FR-083, DOC-07: every valid amount in [text] in order; the app parser decides which one is the transaction. */
    public fun findAll(text: String): List<Money> =
        AMOUNT_PATTERN
            .findAll(text)
            .mapNotNull { match ->
                toMoney(match.groupValues[CAPTURE_GROUP].trimEnd('.', ','))
            }.toList()

    private fun toMoney(fragment: String): Money? {
        val groups = integerPart(fragment).split('.', ',')
        if (!hasValidGrouping(groups)) return null
        return groups.joinToString("").toLongOrNull()?.let { Money(it) }
    }

    // DOC-07: a last separator followed by exactly 2 digits is decimal when the other separator is not before it;
    // otherwise every separator is a thousands separator. The decimal part is dropped (BR-001).
    private fun integerPart(fragment: String): String {
        val lastIndex = fragment.indexOfLast { it == '.' || it == ',' }
        if (lastIndex < 0) return fragment
        val head = fragment.substring(0, lastIndex)
        val isDecimal = fragment.length - lastIndex - 1 == DECIMAL_DIGITS && !head.contains(fragment[lastIndex])
        return if (isDecimal) head else fragment
    }

    // DOC-07: thousands groups are exactly 3 digits; the first group is 1 to 3 digits
    private fun hasValidGrouping(groups: List<String>): Boolean {
        if (groups.size == 1) return true
        val firstGroup = groups.first()
        return firstGroup.length in 1..MAX_FIRST_GROUP && groups.drop(1).all { it.length == GROUP_DIGITS }
    }
}
