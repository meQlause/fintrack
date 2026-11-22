package com.meqlause.fintrack.core.data.mapper

import com.meqlause.fintrack.core.database.entity.MonitoredAppEntity
import com.meqlause.fintrack.core.model.MonitoredApp
import com.meqlause.fintrack.core.model.WalletId

// DOC-02, FR-090: maps a monitored app row to the domain model
internal fun MonitoredAppEntity.toMonitoredApp(): MonitoredApp =
    MonitoredApp(
        packageName = packageName,
        displayName = displayName,
        isEnabled = isEnabled,
        defaultWalletId = defaultWalletId?.let(::WalletId),
        lastNotificationAt = lastNotificationAt?.toInstant(),
    )

// DOC-02, FR-090: maps a monitored app to its row
internal fun MonitoredApp.toEntity(): MonitoredAppEntity =
    MonitoredAppEntity(
        packageName = packageName,
        displayName = displayName,
        isEnabled = isEnabled,
        defaultWalletId = defaultWalletId?.value,
        lastNotificationAt = lastNotificationAt?.toEpochMilli(),
    )
