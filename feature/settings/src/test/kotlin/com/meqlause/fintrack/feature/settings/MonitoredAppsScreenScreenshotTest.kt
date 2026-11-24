package com.meqlause.fintrack.feature.settings

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.util.TimeZone

// FR-081, FR-082, FR-090, S-13: screenshot references of the Aplikasi dipantau previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class MonitoredAppsScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val originalZone = TimeZone.getDefault()

    // FR-090: the last-received time follows the device zone, so the reference is pinned to WIB on every machine
    @Before
    fun pinTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(REFERENCE_ZONE))
    }

    @After
    fun restoreTimeZone() {
        TimeZone.setDefault(originalZone)
    }

    @Test
    fun givenMonitoredAppsInLightThemeWhenRenderedThenItMatchesTheReference() {
        composeRule.setContent { MonitoredAppsLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/MonitoredAppsScreen_light.png")
    }

    @Test
    fun givenMonitoredAppsInDarkThemeWhenRenderedThenItMatchesTheReference() {
        composeRule.setContent { MonitoredAppsDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/MonitoredAppsScreen_dark.png")
    }

    private companion object {
        const val REFERENCE_ZONE = "Asia/Jakarta"
    }
}
