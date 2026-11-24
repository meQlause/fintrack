package com.meqlause.fintrack.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// FR-120, FR-121, S-12: previews of the Pengaturan screen in light and dark themes
private val previewState = SettingsUiState(pinEnabled = true, biometric = BiometricState(supported = true))

@Preview(name = "Settings light", showBackground = true, widthDp = 390, heightDp = 1200)
@Composable
internal fun SettingsLightPreview() {
    FintrackTheme(darkTheme = false) {
        SettingsScreen(state = previewState, callbacks = SettingsCallbacks(), version = "0.1")
    }
}

@Preview(name = "Settings dark", showBackground = true, widthDp = 390, heightDp = 1200)
@Composable
internal fun SettingsDarkPreview() {
    FintrackTheme(darkTheme = true) {
        SettingsScreen(state = previewState, callbacks = SettingsCallbacks(), version = "0.1")
    }
}

// NFR-010: font scale 2.0 variant for the screenshot tests
@Preview(name = "Settings font 200", showBackground = true, fontScale = 2f)
@Composable
internal fun SettingsFontScalePreview() {
    SettingsLightPreview()
}
