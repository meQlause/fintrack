package com.meqlause.fintrack.feature.transaction.detail

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-034, FR-035, S-06: screenshot references of the transaction detail previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class TransactionDetailScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenStoredExpenseInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { TransactionDetailLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/TransactionDetail_light.png")
    }

    @Test
    fun givenStoredExpenseInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { TransactionDetailDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/TransactionDetail_dark.png")
    }
}
