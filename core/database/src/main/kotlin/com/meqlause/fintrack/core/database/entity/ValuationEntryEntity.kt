package com.meqlause.fintrack.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** DOC-02: a manual value of an investment wallet at a point in time (FR-172, ADR-017). */
@Entity(
    tableName = "valuation_entry",
    foreignKeys = [
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["walletId"],
            childColumns = ["walletId"],
            // DOC-02: valuation history belongs to its investment wallet and is deleted with it
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["walletId"])],
)
data class ValuationEntryEntity(
    @PrimaryKey(autoGenerate = true) val valuationId: Long = 0,
    val walletId: Long,
    val value: Long,
    val valuedAt: Long,
    val createdAt: Long,
)
