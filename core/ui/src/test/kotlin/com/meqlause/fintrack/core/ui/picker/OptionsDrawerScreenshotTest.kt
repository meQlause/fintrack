package com.meqlause.fintrack.core.ui.picker

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
class OptionsDrawerScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // S-18: options drawer in light theme, one radio per option
    @Test
    fun `given light theme when rendered then the options drawer matches the reference (S-18)`() {
        composeRule.setContent { OptionsDrawerLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OptionsDrawer_light.png")
    }

    // S-18: options drawer in dark theme
    @Test
    fun `given dark theme when rendered then the options drawer matches the reference (S-18)`() {
        composeRule.setContent { OptionsDrawerDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OptionsDrawer_dark.png")
    }
}
