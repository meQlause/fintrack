package com.meqlause.fintrack.core.model

import java.time.Instant

/** DOC-02: spending limit for a category, or overall when categoryId is null (FR-074). */
public data class Budget(
    public val budgetId: BudgetId,
    public val categoryId: CategoryId?,
    public val limitAmount: Money,
    public val isActive: Boolean = true,
    public val createdAt: Instant,
    public val updatedAt: Instant,
)
