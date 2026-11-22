package com.meqlause.fintrack.core.model

import java.time.Instant

/** DOC-02: a sent budget alert, keyed by budget, period and threshold so it fires once (BR-021, FR-073). */
public data class BudgetAlertLog(
    public val budgetId: BudgetId,
    public val periodStart: Instant,
    public val threshold: Int,
    public val notifiedAt: Instant,
)
