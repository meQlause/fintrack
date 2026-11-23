package com.meqlause.fintrack.core.domain.budget

import com.meqlause.fintrack.core.domain.repository.BudgetAlertLogRepository
import com.meqlause.fintrack.core.model.BudgetAlertLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// FR-073: alert log held in memory; a duplicate key is rejected like the Room primary key
internal class InMemoryBudgetAlertLogRepository : BudgetAlertLogRepository {
    private val rows = MutableStateFlow<List<BudgetAlertLog>>(emptyList())

    override fun observeAll(): Flow<List<BudgetAlertLog>> = rows

    override suspend fun insert(log: BudgetAlertLog) {
        val duplicate =
            rows.value.any {
                it.budgetId == log.budgetId && it.periodStart == log.periodStart && it.threshold == log.threshold
            }
        check(!duplicate) { "duplicate budget alert" }
        rows.update { it + log }
    }
}
