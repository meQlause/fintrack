package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.Budget
import com.meqlause.fintrack.core.model.BudgetId
import kotlinx.coroutines.flow.Flow

/** DOC-03, FR-074: access to spending limits; one budget per category (BR-020). */
public interface BudgetRepository {
    /** DOC-02: every budget in creation order. */
    public fun observeAll(): Flow<List<Budget>>

    /** FR-074: stores a budget and returns its generated id; a second budget for the same category is rejected. */
    public suspend fun insert(budget: Budget): BudgetId

    /** FR-070: stores the changed limit of an existing budget; the budgetId of [budget] selects the row. */
    public suspend fun update(budget: Budget)
}
