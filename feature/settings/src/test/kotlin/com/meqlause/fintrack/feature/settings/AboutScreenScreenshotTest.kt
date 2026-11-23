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

// FR-123, S-12: screenshot references of the Tentang previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class AboutScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenVersionInLightThemeWhenRenderedThenItMatchesTheReference() {
        composeRule.setContent { AboutLightSample() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/AboutScreen_light.png")
    }

    @Test
    fun givenVersionInDarkThemeWhenRenderedThenItMatchesTheReference() {
        composeRule.setContent { AboutDarkSample() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/AboutScreen_dark.png")
    }
}
