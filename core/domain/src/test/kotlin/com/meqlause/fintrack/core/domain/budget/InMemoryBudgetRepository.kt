package com.meqlause.fintrack.core.domain.budget

import com.meqlause.fintrack.core.domain.repository.BudgetRepository
import com.meqlause.fintrack.core.model.Budget
import com.meqlause.fintrack.core.model.BudgetId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// FR-074: budgets held in memory for domain tests
internal class InMemoryBudgetRepository : BudgetRepository {
    private val rows = MutableStateFlow<List<Budget>>(emptyList())
    private var lastId = 0L

    override fun observeAll(): Flow<List<Budget>> = rows

    override suspend fun insert(budget: Budget): BudgetId {
        lastId += 1
        rows.update { it + budget.copy(budgetId = BudgetId(lastId)) }
        return BudgetId(lastId)
    }

    override suspend fun update(budget: Budget) {
        rows.update { list -> list.map { if (it.budgetId == budget.budgetId) budget else it } }
    }
}
