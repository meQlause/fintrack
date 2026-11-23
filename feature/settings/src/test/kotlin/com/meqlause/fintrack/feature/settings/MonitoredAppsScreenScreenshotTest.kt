package com.meqlause.fintrack.feature.settings

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-081, FR-082, FR-090, S-13: screenshot references of the Aplikasi dipantau previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class MonitoredAppsScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenMonitoredAppsInLightThemeWhenRenderedThenItMatchesTheReference() {
        composeRule.setContent { MonitoredAppsLightSample() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/MonitoredAppsScreen_light.png")
    }

    @Test
    fun givenMonitoredAppsInDarkThemeWhenRenderedThenItMatchesTheReference() {
        composeRule.setContent { MonitoredAppsDarkSample() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/MonitoredAppsScreen_dark.png")
    }
}
