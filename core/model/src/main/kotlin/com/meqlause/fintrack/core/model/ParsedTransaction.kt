package com.meqlause.fintrack.core.model

import java.time.Instant

/**
 * FR-083, DOC-07: a transaction extracted from a notification. [merchant] is the display text and
 * [merchantKey] the normalized memory key (FR-088); both are empty when unknown.
 */
public data class ParsedTransaction(
    public val amount: Money,
    public val direction: MoneyDirection,
    public val merchant: String,
    public val merchantKey: String,
    public val occurredAt: Instant,
    public val parserId: String,
)
