package com.meqlause.fintrack.feature.credit.instalment

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-142, S-16: screenshot references of the instalment list previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class InstalmentListScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenActivePlansInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { InstalmentListLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/InstalmentList_light.png")
    }

    @Test
    fun givenActivePlansInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { InstalmentListDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/InstalmentList_dark.png")
    }
}
