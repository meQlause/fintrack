package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.BudgetAlertLogRepository
import com.meqlause.fintrack.core.model.BudgetAlertLog
import kotlinx.coroutines.flow.Flow

/** FR-073: in-memory alert log; a repeated budget, period and threshold is rejected like the Room primary key. */
public class FakeBudgetAlertLogRepository : BudgetAlertLogRepository {
    private val store =
        RowStore<BudgetAlertLog>(
            idOf = { it.threshold.toLong() },
            withId = { log, _ -> log },
            order = compareBy { it.notifiedAt },
        )

    override fun observeAll(): Flow<List<BudgetAlertLog>> = store.observe()

    override suspend fun insert(alertLog: BudgetAlertLog) {
        store.append(alertLog) { same(it, alertLog) }
    }

    private fun same(
        first: BudgetAlertLog,
        second: BudgetAlertLog,
    ): Boolean =
        first.budgetId == second.budgetId &&
            first.periodStart == second.periodStart &&
            first.threshold == second.threshold
}
