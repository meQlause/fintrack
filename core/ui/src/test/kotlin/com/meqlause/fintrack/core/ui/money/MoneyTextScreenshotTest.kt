package com.meqlause.fintrack.core.ui.money

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
class MoneyTextScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // DOC-08: money is coloured by direction and shown with its sign, in light and dark themes
    @Test
    fun `given light theme when rendered then the money text matches the reference (DOC-08)`() {
        composeRule.setContent { MoneyTextLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/MoneyText_light.png")
    }

    @Test
    fun `given dark theme when rendered then the money text matches the reference (DOC-08)`() {
        composeRule.setContent { MoneyTextDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/MoneyText_dark.png")
    }
}
