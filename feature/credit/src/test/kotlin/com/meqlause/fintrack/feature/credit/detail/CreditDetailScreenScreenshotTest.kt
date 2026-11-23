package com.meqlause.fintrack.feature.credit.detail

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-131 to FR-133, S-14, S-15: screenshot references of the credit wallet previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class CreditDetailScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenDueBillInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { CreditDetailLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/CreditDetail_light.png")
    }

    @Test
    fun givenDueBillInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { CreditDetailDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/CreditDetail_dark.png")
    }

    @Test
    fun givenPaySheetOpenInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { CreditPaySheetLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/CreditPaySheet_light.png")
    }
}
