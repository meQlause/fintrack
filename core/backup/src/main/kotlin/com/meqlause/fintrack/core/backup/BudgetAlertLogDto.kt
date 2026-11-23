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
        fun from(log: BudgetAlertLog): BudgetAlertLogDto =
            BudgetAlertLogDto(
                budgetId = log.budgetId.value,
                periodStart = log.periodStart.toString(),
                threshold = log.threshold,
                notifiedAt = log.notifiedAt.toString(),
            )
    }
}
