package com.meqlause.fintrack.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// FR-120, FR-121, S-12: previews of the Pengaturan screen in light and dark themes
@Composable
internal fun SettingsLightSample() {
    FintrackTheme(darkTheme = false) {
        SettingsScreen(state = SettingsUiState(), callbacks = SettingsCallbacks())
    }
}

@Composable
internal fun SettingsDarkSample() {
    FintrackTheme(darkTheme = true) {
        SettingsScreen(state = SettingsUiState(), callbacks = SettingsCallbacks())
    }
}

@Preview(name = "Settings light", showBackground = true)
@Composable
internal fun SettingsLightPreview() {
    SettingsLightSample()
}

@Preview(name = "Settings dark", showBackground = true)
@Composable
internal fun SettingsDarkPreview() {
    SettingsDarkSample()
}

// NFR-010: font scale 2.0 variant for the screenshot tests
@Preview(name = "Settings font 200", showBackground = true, fontScale = 2f)
@Composable
internal fun SettingsFontScalePreview() {
    SettingsLightSample()
}
