package com.meqlause.fintrack.feature.inbox

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

    // NFR-010: font scale 2.0 variant
    @Test
    fun `given font scale 2 when rendered then the inbox matches the reference (NFR-010)`() {
        composeRule.setContent { WithFontScaleTwo { InboxFontScalePreview() } }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/Inbox_fontScale2.png")
    }

    @Test
    fun `given no pending items when rendered then the empty inbox matches the reference (FR-087)`() {
        composeRule.setContent { InboxEmptyPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/Inbox_empty.png")
    }

    @Test
    fun `given notification access off when rendered then the inbox matches the reference (FR-087)`() {
        composeRule.setContent { InboxAccessOffPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/Inbox_accessOff.png")
    }

    @Composable
    private fun WithFontScaleTwo(content: @Composable () -> Unit) {
        val density = LocalDensity.current.density
        CompositionLocalProvider(LocalDensity provides Density(density = density, fontScale = 2f)) {
            content()
        }
    }
}
