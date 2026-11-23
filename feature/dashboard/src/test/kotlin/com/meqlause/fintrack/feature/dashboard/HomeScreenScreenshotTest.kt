package com.meqlause.fintrack.feature.dashboard

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-040..043, S-03: screenshot references of the Beranda previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class HomeScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenSuccessStateInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HomeSuccessLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/HomeSuccess_light.png")
    }

    @Test
    fun givenSuccessStateInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HomeSuccessDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/HomeSuccess_dark.png")
    }

    @Test
    fun givenEmptyStateInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HomeEmptyLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/HomeEmpty_light.png")
    }

    @Test
    fun givenEmptyStateInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HomeEmptyDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/HomeEmpty_dark.png")
    }

    @Test
    fun givenHiddenAmountsInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HomeHiddenLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/HomeHidden_light.png")
    }

    @Test
    fun givenSummaryDrawerInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HomeSummaryDrawerDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/HomeSummary_dark.png")
    }

    @Test
    fun givenCategoryDrawerInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HomeCategoriesDrawerLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/HomeCategories_light.png")
    }
}
