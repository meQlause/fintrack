package com.meqlause.fintrack.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// FR-123, S-12: previews of the Tentang screen in light and dark themes
@Preview(name = "About light", showBackground = true)
@Composable
internal fun AboutLightPreview() {
    FintrackTheme(darkTheme = false) {
        AboutScreen(version = "1.0", onBack = {})
    }
}

@Preview(name = "About dark", showBackground = true)
@Composable
internal fun AboutDarkPreview() {
    FintrackTheme(darkTheme = true) {
        AboutScreen(version = "1.0", onBack = {})
    }
}
