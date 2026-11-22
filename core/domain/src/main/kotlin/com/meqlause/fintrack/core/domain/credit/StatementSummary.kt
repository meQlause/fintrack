package com.meqlause.fintrack.core.domain.credit

import com.meqlause.fintrack.core.model.StatementStatus

/** BR-033, BR-034: one statement with its period, amounts and computed status. */
public data class StatementSummary(
    public val period: StatementPeriod,
    public val breakdown: StatementAmounts,
    public val status: StatementStatus,
)
