package com.meqlause.fintrack.core.data.mapper

import com.meqlause.fintrack.core.database.entity.BudgetAlertLogEntity
import com.meqlause.fintrack.core.model.BudgetAlertLog
import com.meqlause.fintrack.core.model.BudgetId
import java.time.Instant

// DOC-02, FR-073: maps a sent-alert row to the domain model
internal fun BudgetAlertLogEntity.toLog(): BudgetAlertLog =
    BudgetAlertLog(
        budgetId = BudgetId(budgetId),
        periodStart = Instant.ofEpochMilli(periodStart),
        threshold = threshold,
        notifiedAt = Instant.ofEpochMilli(notifiedAt),
    )

// DOC-02, FR-073: maps a sent alert to its row
internal fun BudgetAlertLog.toEntity(): BudgetAlertLogEntity =
    BudgetAlertLogEntity(
        budgetId = budgetId.value,
        periodStart = periodStart.toEpochMilli(),
        threshold = threshold,
        notifiedAt = notifiedAt.toEpochMilli(),
    )
