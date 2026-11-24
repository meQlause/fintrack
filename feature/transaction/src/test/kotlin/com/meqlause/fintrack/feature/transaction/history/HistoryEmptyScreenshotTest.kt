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

// S-05: screenshot reference of the empty Riwayat
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class HistoryEmptyScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenMonthWithoutTransactionsWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HistoryEmptyLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/HistoryEmpty_light.png")
    }
}
