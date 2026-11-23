package com.meqlause.fintrack.core.domain.credit

import com.meqlause.fintrack.core.model.Money

/** FR-140, BR-038: the instalment terms the editor collects with a purchase on a credit wallet. */
public data class InstalmentRequest(
    public val tenorMonths: Int,
    public val adminFee: Money,
) {
    init {
        require(tenorMonths in MIN_TENOR..MAX_TENOR) { "tenor must be 2..36 (FR-140)" }
    }

    private companion object {
        const val MIN_TENOR = 2
        const val MAX_TENOR = 36
    }
}
