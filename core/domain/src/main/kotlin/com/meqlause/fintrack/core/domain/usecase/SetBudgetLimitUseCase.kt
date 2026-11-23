package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.BudgetRepository
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Budget
import com.meqlause.fintrack.core.model.BudgetId
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

/** FR-070, FR-074, BR-020: sets the monthly limit of one category, or of the overall budget when categoryId is null. */
public class SetBudgetLimitUseCase
    @Inject
    constructor(
        private val budgets: BudgetRepository,
        private val clock: Clock,
    ) {
        /** FR-070: an existing budget for the same target is updated, so a category never gets a second row. */
        public suspend operator fun invoke(
            categoryId: CategoryId?,
            limit: Money,
        ) {
            require(limit.isTransactionAmount()) { "budget limit must be 1..999999999999 rupiah (BR-001)" }
            val now = Instant.now(clock)
            val existing = budgets.observeAll().first().firstOrNull { it.categoryId == categoryId }
            if (existing == null) {
                budgets.insert(Budget(BudgetId(0L), categoryId, limit, true, now, now))
            } else {
                budgets.update(existing.copy(limitAmount = limit, isActive = true, updatedAt = now))
            }
        }
    }
