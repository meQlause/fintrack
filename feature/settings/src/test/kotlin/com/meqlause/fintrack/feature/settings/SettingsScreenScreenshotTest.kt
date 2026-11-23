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

// FR-120, FR-121, S-12: screenshot references of the Pengaturan previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w390dp-h1200dp")
class SettingsScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenDefaultsInLightThemeWhenRenderedThenItMatchesTheReference() {
        composeRule.setContent { SettingsLightSample() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/SettingsScreen_light.png")
    }

    @Test
    fun givenDefaultsInDarkThemeWhenRenderedThenItMatchesTheReference() {
        composeRule.setContent { SettingsDarkSample() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/SettingsScreen_dark.png")
    }
}
