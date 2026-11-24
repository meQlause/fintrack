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

// S-05: screenshot references of the period picker previews
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w390dp-h800dp")
class HistoryPickerScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenPeriodPickerInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HistoryPickerLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/HistoryPicker_light.png")
    }

    @Test
    fun givenPeriodPickerInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HistoryPickerDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/HistoryPicker_dark.png")
    }
}
