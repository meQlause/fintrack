package com.meqlause.fintrack.core.data.mapper

import com.meqlause.fintrack.core.database.entity.AuditColumns
import com.meqlause.fintrack.core.database.entity.BudgetEntity
import com.meqlause.fintrack.core.model.Budget
import com.meqlause.fintrack.core.model.BudgetId
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money

// DOC-02, FR-074: maps a budget row to the domain model; a null category means the overall budget
internal fun BudgetEntity.toBudget(): Budget =
    Budget(
        budgetId = BudgetId(budgetId),
        categoryId = categoryId?.let(::CategoryId),
        limitAmount = Money(limitAmount),
        isActive = isActive,
        createdAt = audit.createdAt.toInstant(),
        updatedAt = audit.updatedAt.toInstant(),
    )

// DOC-02, FR-074: maps a budget to its row
internal fun Budget.toEntity(): BudgetEntity =
    BudgetEntity(
        budgetId = budgetId.value,
        categoryId = categoryId?.value,
        limitAmount = limitAmount.rupiah,
        isActive = isActive,
        audit = AuditColumns(createdAt = createdAt.toEpochMilli(), updatedAt = updatedAt.toEpochMilli()),
    )
