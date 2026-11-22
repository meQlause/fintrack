package com.meqlause.fintrack.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** DOC-02: a spending limit per category, or overall when categoryId is null (FR-074). */
@Entity(
    tableName = "budget",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index(value = ["categoryId"], unique = true)],
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val budgetId: Long = 0,
    val categoryId: Long?,
    val limitAmount: Long,
    val isActive: Boolean = true,
    @Embedded val audit: AuditColumns,
)
