package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.Budget
import com.meqlause.fintrack.core.model.BudgetId
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import kotlinx.serialization.Serializable

// DOC-11: one budget row in the backup
@Serializable
internal data class BudgetDto(
    val budgetId: Long,
    val categoryId: Long?,
    val limitAmount: Long,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
) {
    fun toModel(): Budget =
        Budget(
            budgetId = BudgetId(budgetId),
            categoryId = categoryId?.let(::CategoryId),
            limitAmount = Money(limitAmount),
            isActive = isActive,
            createdAt = BackupValues.instant(createdAt),
            updatedAt = BackupValues.instant(updatedAt),
        )

    companion object {
        fun from(budget: Budget): BudgetDto =
            BudgetDto(
                budgetId = budget.budgetId.value,
                categoryId = budget.categoryId?.value,
                limitAmount = budget.limitAmount.rupiah,
                isActive = budget.isActive,
                createdAt = budget.createdAt.toString(),
                updatedAt = budget.updatedAt.toString(),
            )
    }
}
