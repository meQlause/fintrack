package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.BudgetAlertLog
import kotlinx.coroutines.flow.Flow

/** DOC-03, FR-073: record of budget alerts already sent, read to avoid repeating them (BR-021). */
public interface BudgetAlertLogRepository {
    /** DOC-02: every sent alert, oldest first. */
    public fun observeAll(): Flow<List<BudgetAlertLog>>

    /** FR-073: records a sent alert; the same budget, period and threshold is never recorded twice. */
    public suspend fun insert(alertLog: BudgetAlertLog)
}
