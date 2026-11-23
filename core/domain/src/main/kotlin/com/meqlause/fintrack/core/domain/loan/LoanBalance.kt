package com.meqlause.fintrack.core.domain.loan

import com.meqlause.fintrack.core.model.Money

// BR-041: outstanding = principal minus the repayments, never below zero
public data class LoanBalance(
    public val principal: Money,
    public val repaid: Money,
) {
    public val outstanding: Money
        get() = Money((principal.rupiah - repaid.rupiah).coerceAtLeast(0L))
}
