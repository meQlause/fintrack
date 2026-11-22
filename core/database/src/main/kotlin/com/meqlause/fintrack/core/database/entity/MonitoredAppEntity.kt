package com.meqlause.fintrack.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** DOC-02: a notification source the app listens to; its package name is the key (FR-090). */
@Entity(
    tableName = "monitored_app",
    foreignKeys = [
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["walletId"],
            childColumns = ["defaultWalletId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index(value = ["defaultWalletId"])],
)
data class MonitoredAppEntity(
    @PrimaryKey val packageName: String,
    val displayName: String,
    val isEnabled: Boolean,
    val defaultWalletId: Long?,
    val lastNotificationAt: Long?,
)
