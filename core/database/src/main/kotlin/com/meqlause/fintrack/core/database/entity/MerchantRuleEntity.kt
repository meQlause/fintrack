package com.meqlause.fintrack.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** DOC-02: merchant memory that suggests a category, and optionally a wallet, for a normalized merchant (FR-088). */
@Entity(
    tableName = "merchant_rule",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["walletId"],
            childColumns = ["walletId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["merchantKey"], unique = true),
        Index(value = ["categoryId"]),
        Index(value = ["walletId"]),
    ],
)
data class MerchantRuleEntity(
    @PrimaryKey(autoGenerate = true) val merchantRuleId: Long = 0,
    val merchantKey: String,
    val categoryId: Long,
    val walletId: Long?,
    val hitCount: Int = 0,
    @Embedded val audit: AuditColumns,
)
