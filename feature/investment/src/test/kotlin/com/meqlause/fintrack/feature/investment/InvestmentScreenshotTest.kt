package com.meqlause.fintrack.feature.investment

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// S-08, S-20: the previews are screenshot-tested in light and dark, the reference images are written by Roborazzi
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w390dp-h844dp")
class InvestmentScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `given investasi with accounts in light theme when rendered then the tab matches the reference (FR-173)`() {
        composeRule.setContent { InvestmentSuccessLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/Investment_tab_light.png")
    }

    @Test
    fun `given investasi with accounts in dark theme when rendered then the tab matches the reference (FR-173)`() {
        composeRule.setContent { InvestmentSuccessDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/Investment_tab_dark.png")
    }

    @Test
    fun `given investasi detail in light theme when rendered then S-20 matches the reference (FR-173)`() {
        composeRule.setContent { InvestmentDetailLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/Investment_detail_light.png")
    }

    @Test
    fun `given investasi detail in dark theme when rendered then S-20 matches the reference (FR-173)`() {
        composeRule.setContent { InvestmentDetailDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/Investment_detail_dark.png")
    }

    @Test
    fun `given investasi without accounts when rendered then the empty tab matches the reference (FR-173)`() {
        composeRule.setContent { InvestmentEmptyLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/Investment_empty_light.png")
    }
}
