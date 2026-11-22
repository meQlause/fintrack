package com.meqlause.fintrack.core.model

import java.time.Instant

/** DOC-02: an app whose notifications are read; packageName is its key (FR-090). */
public data class MonitoredApp(
    public val packageName: String,
    public val displayName: String,
    public val isEnabled: Boolean,
    public val defaultWalletId: WalletId?,
    public val lastNotificationAt: Instant?,
)
