package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.mapper.toBudget
import com.meqlause.fintrack.core.data.mapper.toEntity
import com.meqlause.fintrack.core.database.dao.BudgetDao
import com.meqlause.fintrack.core.domain.repository.BudgetRepository
import com.meqlause.fintrack.core.model.Budget
import com.meqlause.fintrack.core.model.BudgetId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** DOC-03, ADR-007, FR-074: spending limits in Room; one budget per category is enforced by a unique index. */
public class LocalBudgetRepository(
    private val budgetDao: BudgetDao,
) : BudgetRepository {
    override fun observeAll(): Flow<List<Budget>> = budgetDao.observeAll().map { rows -> rows.map { it.toBudget() } }

    override suspend fun insert(budget: Budget): BudgetId = BudgetId(budgetDao.insert(budget.toEntity()))

    override suspend fun update(budget: Budget) = budgetDao.update(budget.toEntity())
}
