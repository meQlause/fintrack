package com.meqlause.fintrack.core.domain.investment

import com.meqlause.fintrack.core.model.Money
import java.time.Instant

/** BR-051: modal, current value and gain of one investment; lastValuedAt is null before any valuation. */
public data class InvestmentValuation(
    public val modal: Money,
    public val currentValue: Money,
    public val lastValuedAt: Instant?,
) {
    /** BR-051: gain = currentValue - modal. */
    public val gain: Money
        get() = currentValue - modal

    /** BR-051: gain in tenths of a percent (40 means 4,0%), integer math; 0 when nothing was put in. */
    public val gainTenthPercent: Long
        get() = if (modal.rupiah <= 0L) 0L else gain.rupiah * PERCENT_TENTHS / modal.rupiah

    private companion object {
        const val PERCENT_TENTHS = 1000L
    }
}
