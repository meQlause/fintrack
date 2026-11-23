package com.meqlause.fintrack.feature.settings

import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.picker.PickerOption
import java.time.Instant

// FR-081, FR-082: one supported app as S-13 shows it; wallet name is null when no default is chosen
internal data class MonitoredAppRow(
    val packageName: String,
    val displayName: String,
    val isEnabled: Boolean,
    val defaultWalletId: WalletId?,
    val defaultWalletName: String?,
    val lastNotificationAt: Instant?,
)

// FR-081, FR-082, FR-090: what S-13 renders; pickingPackage is the app whose wallet drawer is open, or null
internal data class MonitoredAppsUiState(
    val accessGranted: Boolean = false,
    val ignoredCount: Int = 0,
    val unrecognizedCount: Int = 0,
    val apps: List<MonitoredAppRow> = emptyList(),
    val wallets: List<PickerOption> = emptyList(),
    val pickingPackage: String? = null,
)
