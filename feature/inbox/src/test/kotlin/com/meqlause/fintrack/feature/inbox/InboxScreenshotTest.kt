package com.meqlause.fintrack.feature.inbox

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// S-07: the Kotak masuk previews are screenshot-tested in light and dark, references are written by Roborazzi
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w390dp-h844dp")
class InboxScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `given pending items in light theme when rendered then the inbox matches the reference (FR-087)`() {
        composeRule.setContent { InboxLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/Inbox_light.png")
    }

    @Test
    fun `given pending items in dark theme when rendered then the inbox matches the reference (FR-087)`() {
        composeRule.setContent { InboxDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/Inbox_dark.png")
    }
}
