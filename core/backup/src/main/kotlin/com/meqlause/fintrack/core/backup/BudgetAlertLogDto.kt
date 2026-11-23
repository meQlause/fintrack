package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.BudgetAlertLog
import com.meqlause.fintrack.core.model.BudgetId
import kotlinx.serialization.Serializable

// DOC-11: one sent budget alert in the backup, keyed by budget, period and threshold
@Serializable
internal data class BudgetAlertLogDto(
    val budgetId: Long,
    val periodStart: String,
    val threshold: Int,
    val notifiedAt: String,
) {
    fun toModel(): BudgetAlertLog =
        BudgetAlertLog(
            budgetId = BudgetId(budgetId),
            periodStart = BackupValues.instant(periodStart),
            threshold = threshold,
            notifiedAt = BackupValues.instant(notifiedAt),
        )

    companion object {
        fun from(alertLog: BudgetAlertLog): BudgetAlertLogDto =
            BudgetAlertLogDto(
                budgetId = alertLog.budgetId.value,
                periodStart = alertLog.periodStart.toString(),
                threshold = alertLog.threshold,
                notifiedAt = alertLog.notifiedAt.toString(),
            )
    }
}
