package com.meqlause.fintrack.core.designsystem.component

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
class FintrackTabHeaderScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // DOC-09: the shared tab header (32 dp tile, 20 sp title) is screenshot-tested in both themes
    @Test
    fun `given light theme when rendered then the tab header matches the reference (DOC-09)`() {
        composeRule.setContent { TabHeaderLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/FintrackTabHeader_light.png")
    }

    @Test
    fun `given dark theme when rendered then the tab header matches the reference (DOC-09)`() {
        composeRule.setContent { TabHeaderDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/FintrackTabHeader_dark.png")
    }
}
