package com.meqlause.fintrack.feature.lock

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.lock.navigation.PinSetupMode

// S-02, FR-100: preview and screenshot host for the lock screen with a wrong PIN and six digits typed
@Composable
internal fun LockPreviewScreen(darkTheme: Boolean) {
    FintrackTheme(darkTheme = darkTheme) {
        LockScreen(
            state = LockUiState(digits = "1234", notice = PinNotice.WRONG, shakeCount = 0),
            onDigit = {},
            onBackspace = {},
        )
    }
}

// S-12, FR-100: preview and screenshot host for the PIN setup on the new-PIN step
@Composable
internal fun PinSetupPreviewScreen(darkTheme: Boolean) {
    FintrackTheme(darkTheme = darkTheme) {
        PinSetupScreen(
            state = PinSetupUiState(mode = PinSetupMode.SET, step = PinStep.NEW, digits = "12"),
            onDigit = {},
            onBackspace = {},
            onCancel = {},
        )
    }
}

@Preview(name = "Lock light", showBackground = true)
@Composable
private fun LockLightPreview() {
    LockPreviewScreen(darkTheme = false)
}

@Preview(name = "Lock dark", showBackground = true)
@Composable
private fun LockDarkPreview() {
    LockPreviewScreen(darkTheme = true)
}
