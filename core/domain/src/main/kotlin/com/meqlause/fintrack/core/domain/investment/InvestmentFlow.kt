package com.meqlause.fintrack.core.domain.investment

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import java.time.Instant

/** BR-051: one transfer seen from an investment wallet; CREDIT is money put in, DEBIT is money taken out. */
public data class InvestmentFlow(
    public val occurredAt: Instant,
    public val amount: Money,
    public val direction: MoneyDirection,
)
