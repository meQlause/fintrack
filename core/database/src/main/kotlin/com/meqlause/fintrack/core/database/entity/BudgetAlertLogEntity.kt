package com.meqlause.fintrack.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey

/** DOC-02: a sent budget alert, keyed by budget, period and threshold so each fires once (FR-073). */
@Entity(
    tableName = "budget_alert_log",
    primaryKeys = ["budgetId", "periodStart", "threshold"],
    foreignKeys = [
        ForeignKey(
            entity = BudgetEntity::class,
            parentColumns = ["budgetId"],
            childColumns = ["budgetId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class BudgetAlertLogEntity(
    val budgetId: Long,
    val periodStart: Long,
    val threshold: Int,
    val notifiedAt: Long,
)
