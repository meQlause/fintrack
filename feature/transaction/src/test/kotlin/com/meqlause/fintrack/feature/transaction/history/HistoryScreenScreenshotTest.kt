package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-036, FR-037, S-05: screenshot references of the Riwayat previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class HistoryScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenSampleListInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HistoryScreenLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/History_light.png")
    }

    @Test
    fun givenSampleListInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HistoryScreenDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/History_dark.png")
    }
}
