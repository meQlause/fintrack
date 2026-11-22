package com.meqlause.fintrack.core.designsystem

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ThemeSwatchScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // DOC-06 screenshot tests: each state is captured in light and dark
    @Test
    fun `given light theme when rendered then the swatch matches the reference (DOC-06)`() {
        composeRule.setContent { ThemeSwatch(darkTheme = false) }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/ThemeSwatch_light.png")
    }

    @Test
    fun `given dark theme when rendered then the swatch matches the reference (DOC-06)`() {
        composeRule.setContent { ThemeSwatch(darkTheme = true) }

        composeRule.onRoot().captureRoboImage("src/test/screenshots/ThemeSwatch_dark.png")
    }
}
