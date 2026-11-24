package com.meqlause.fintrack.core.designsystem.icon

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
class IconCircleScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // DOC-08 principle 4: the category tile is screenshot-tested in light and dark
    @Test
    fun `given light theme when rendered then the icon circle matches the reference (DOC-08)`() {
        composeRule.setContent { IconCircleLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/IconCircle_light.png")
    }

    @Test
    fun `given dark theme when rendered then the icon circle matches the reference (DOC-08)`() {
        composeRule.setContent { IconCircleDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/IconCircle_dark.png")
    }
}
