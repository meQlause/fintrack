package com.meqlause.fintrack.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.meqlause.fintrack.core.model.TransactionType

/** DOC-02: a shortcut that pre-fills a draft transaction when applied (FR-039, BR-047). */
@Entity(
    tableName = "quick_entry",
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
        Index(value = ["categoryId"]),
        Index(value = ["walletId"]),
    ],
)
data class QuickEntryEntity(
    @PrimaryKey(autoGenerate = true) val quickEntryId: Long = 0,
    val title: String,
    val sortOrder: Int,
    @Embedded val target: Target,
    @Embedded val audit: AuditColumns,
) {
    /** DOC-02: the draft the shortcut fills: an expense or income, its amount, category and wallet (BR-006). */
    data class Target(
        val type: TransactionType,
        val amount: Long,
        val categoryId: Long,
        val walletId: Long,
    )
}
