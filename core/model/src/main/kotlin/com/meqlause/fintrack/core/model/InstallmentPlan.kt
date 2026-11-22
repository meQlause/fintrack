package com.meqlause.fintrack.core.model

/** DOC-02: a 0% instalment on a credit wallet (FR-140 to FR-146). Portions are computed (BR-035, BR-036). */
public data class InstallmentPlan(
    public val installmentPlanId: InstallmentPlanId,
    public val purchase: Purchase,
    public val schedule: Schedule,
    public val status: InstallmentStatus,
    public val audit: Audit,
)
