package com.meqlause.fintrack.feature.loan.list

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-153, S-17: screenshot references of the loan list previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class LoanListScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenLoansInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { LoanListLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/LoanList_light.png")
    }

    @Test
    fun givenLoansInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { LoanListDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/LoanList_dark.png")
    }
}
