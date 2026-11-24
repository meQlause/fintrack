package com.meqlause.fintrack.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-07, S-12: previews of the battery help page in light and dark themes
@Preview(name = "Battery help light", showBackground = true)
@Composable
internal fun BatteryHelpLightPreview() {
    FintrackTheme(darkTheme = false) {
        BatteryHelpScreen(
            optimisationIgnored = false,
            guide = batteryGuideFor("Xiaomi"),
            onOpenSettings = {},
            onBack = {},
        )
    }
}

@Preview(name = "Battery help dark", showBackground = true)
@Composable
internal fun BatteryHelpDarkPreview() {
    FintrackTheme(darkTheme = true) {
        BatteryHelpScreen(
            optimisationIgnored = true,
            guide = batteryGuideFor("Samsung"),
            onOpenSettings = {},
            onBack = {},
        )
    }
}
