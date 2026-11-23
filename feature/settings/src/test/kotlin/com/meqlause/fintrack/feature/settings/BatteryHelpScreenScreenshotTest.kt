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

// DOC-07, S-12: screenshot references of the battery help previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class BatteryHelpScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenXiaomiInLightThemeWhenRenderedThenItMatchesTheReference() {
        composeRule.setContent { BatteryHelpLightSample() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/BatteryHelpScreen_light.png")
    }

    @Test
    fun givenSamsungInDarkThemeWhenRenderedThenItMatchesTheReference() {
        composeRule.setContent { BatteryHelpDarkSample() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/BatteryHelpScreen_dark.png")
    }
}
