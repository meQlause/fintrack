package com.meqlause.fintrack.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
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
        composeRule.setContent { SettingsLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/SettingsScreen_light.png")
    }

    @Test
    fun givenDefaultsInDarkThemeWhenRenderedThenItMatchesTheReference() {
        composeRule.setContent { SettingsDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/SettingsScreen_dark.png")
    }

    // NFR-010: font scale 2.0 variant
    @Test
    fun `given font scale 2 when rendered then the settings screen matches the reference (NFR-010)`() {
        composeRule.setContent { WithFontScaleTwo { SettingsLightPreview() } }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/SettingsScreen_fontScale2.png")
    }

    @Composable
    private fun WithFontScaleTwo(content: @Composable () -> Unit) {
        val density = LocalDensity.current.density
        CompositionLocalProvider(LocalDensity provides Density(density = density, fontScale = 2f)) {
            content()
        }
    }
}
