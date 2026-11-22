package com.meqlause.fintrack.core.parser

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import java.time.Instant

/** FR-083, DOC-07: a transaction extracted from a notification; merchant is empty when unknown. */
public data class ParsedTransaction(
    public val amount: Money,
    public val direction: MoneyDirection,
    public val merchant: String,
    public val occurredAt: Instant,
    public val parserId: String,
)
