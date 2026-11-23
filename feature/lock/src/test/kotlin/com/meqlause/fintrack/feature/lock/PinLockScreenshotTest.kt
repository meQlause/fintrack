package com.meqlause.fintrack.feature.lock

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class PinLockScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // S-02, FR-100: the lock screen is screenshot-tested in light and dark (DOC-06, DOC-08)
    @Test
    fun `given lock screen in light theme when rendered then it matches the reference (FR-100)`() {
        composeRule.setContent { LockPreviewScreen(darkTheme = false) }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/PinLock_light.png")
    }

    @Test
    fun `given lock screen in dark theme when rendered then it matches the reference (FR-100)`() {
        composeRule.setContent { LockPreviewScreen(darkTheme = true) }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/PinLock_dark.png")
    }

    // S-12, FR-100: the PIN setup on the new-PIN step with its recovery warning
    @Test
    fun `given pin setup in light theme when rendered then it matches the reference (FR-100)`() {
        composeRule.setContent { PinSetupPreviewScreen(darkTheme = false) }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/PinSetup_light.png")
    }
}
