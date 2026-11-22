package com.meqlause.fintrack.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import com.github.takahirom.roborazzi.captureRoboImage
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.theme.TokenSwatch
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h1800dp")
class TokenSwatchScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // DOC-06 screenshot tests: each state is captured in light and dark
    @Test
    fun `given light theme when rendered then the token swatch matches the reference (DOC-08)`() {
        composeRule.setContent { FintrackTheme(darkTheme = false) { TokenSwatch() } }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/TokenSwatch_light.png")
    }

    @Test
    fun `given dark theme when rendered then the token swatch matches the reference (DOC-08)`() {
        composeRule.setContent { FintrackTheme(darkTheme = true) { TokenSwatch() } }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/TokenSwatch_dark.png")
    }

    // NFR-010: the swatch stays legible when the user's font scale is doubled
    @Test
    fun `given font scale 2 when rendered then the token swatch matches the reference (NFR-010)`() {
        composeRule.setContent {
            WithFontScaleTwo { FintrackTheme(darkTheme = false) { TokenSwatch() } }
        }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/TokenSwatch_fontScale2.png")
    }

    @Composable
    private fun WithFontScaleTwo(content: @Composable () -> Unit) {
        val density = LocalDensity.current.density
        CompositionLocalProvider(LocalDensity provides Density(density = density, fontScale = 2f)) {
            content()
        }
    }
}
