package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.BudgetRepository
import com.meqlause.fintrack.core.model.Budget
import com.meqlause.fintrack.core.model.BudgetId
import kotlinx.coroutines.flow.Flow

/** ADR-007, FR-074: in-memory budgets; one budget per category, overall budgets may repeat. */
public class FakeBudgetRepository : BudgetRepository {
    private val store =
        RowStore<Budget>(
            idOf = { it.budgetId.value },
            withId = { budget, newId -> budget.copy(budgetId = BudgetId(newId)) },
            order = compareBy { it.budgetId.value },
        )

    override fun observeAll(): Flow<List<Budget>> = store.observe()

    override suspend fun insert(budget: Budget): BudgetId =
        BudgetId(
            store.append(budget) { existing -> budget.categoryId != null && existing.categoryId == budget.categoryId },
        )
}
