package com.meqlause.fintrack.core.domain.credit

import com.meqlause.fintrack.core.model.Money
import java.time.LocalDate
import java.time.YearMonth

/** BR-035 to BR-037: the data an instalment plan needs for statement math; portions are computed, not stored. */
public data class CreditInstalment(
    public val purchaseDate: LocalDate,
    public val principal: Money,
    public val tenorMonths: Int,
    public val firstStatementMonth: YearMonth,
    public val earlyPayoffMonth: YearMonth? = null,
) {
    init {
        require(tenorMonths in MIN_TENOR..MAX_TENOR) { "tenor must be 2..36 (FR-140)" }
    }

    private companion object {
        const val MIN_TENOR = 2
        const val MAX_TENOR = 36
    }
}
