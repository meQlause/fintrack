package com.meqlause.fintrack.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.picker.PickerOption
import java.time.Instant

// FR-081, FR-082, FR-090, S-13: previews of the Aplikasi dipantau screen in light and dark themes
internal val monitoredPreviewState =
    MonitoredAppsUiState(
        accessGranted = true,
        ignoredCount = 12,
        unrecognizedCount = 3,
        apps =
            listOf(
                MonitoredAppRow(
                    packageName = "com.bca",
                    displayName = "BCA mobile",
                    isEnabled = true,
                    defaultWalletId = WalletId(2L),
                    defaultWalletName = "BCA",
                    lastNotificationAt = Instant.parse("2026-11-23T08:00:00Z"),
                ),
                MonitoredAppRow(
                    packageName = "com.gojek.gopay",
                    displayName = "GoPay",
                    isEnabled = false,
                    defaultWalletId = null,
                    defaultWalletName = null,
                    lastNotificationAt = null,
                ),
            ),
        wallets = listOf(PickerOption(optionKey = "2", label = "BCA")),
    )

@Composable
internal fun MonitoredAppsLightSample() {
    FintrackTheme(darkTheme = false) {
        MonitoredAppsScreen(state = monitoredPreviewState, callbacks = MonitoredAppsCallbacks())
    }
}

@Composable
internal fun MonitoredAppsDarkSample() {
    FintrackTheme(darkTheme = true) {
        MonitoredAppsScreen(state = monitoredPreviewState, callbacks = MonitoredAppsCallbacks())
    }
}

@Preview(name = "Monitored apps light", showBackground = true)
@Composable
internal fun MonitoredAppsLightPreview() {
    MonitoredAppsLightSample()
}

@Preview(name = "Monitored apps dark", showBackground = true)
@Composable
internal fun MonitoredAppsDarkPreview() {
    MonitoredAppsDarkSample()
}
