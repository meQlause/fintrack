package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.MonitoredApp
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.serialization.Serializable

// DOC-11: one monitored app in the backup; packageName is its key
@Serializable
internal data class MonitoredAppDto(
    val packageName: String,
    val displayName: String,
    val isEnabled: Boolean,
    val defaultWalletId: Long?,
    val lastNotificationAt: String?,
) {
    fun toModel(): MonitoredApp =
        MonitoredApp(
            packageName = packageName,
            displayName = displayName,
            isEnabled = isEnabled,
            defaultWalletId = defaultWalletId?.let(::WalletId),
            lastNotificationAt = lastNotificationAt?.let(BackupValues::instant),
        )

    companion object {
        fun from(app: MonitoredApp): MonitoredAppDto =
            MonitoredAppDto(
                packageName = app.packageName,
                displayName = app.displayName,
                isEnabled = app.isEnabled,
                defaultWalletId = app.defaultWalletId?.value,
                lastNotificationAt = app.lastNotificationAt?.toString(),
            )
    }
}
