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
class DayGridDrawerScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // S-18: 1-28 day grid in light theme, selected day highlighted
    @Test
    fun `given light theme when rendered then the day grid matches the reference (S-18)`() {
        composeRule.setContent { DayGridDrawerLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/DayGridDrawer_light.png")
    }

    // S-18: 1-28 day grid in dark theme
    @Test
    fun `given dark theme when rendered then the day grid matches the reference (S-18)`() {
        composeRule.setContent { DayGridDrawerDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/DayGridDrawer_dark.png")
    }
}
