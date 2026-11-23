package com.meqlause.fintrack.feature.dashboard

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// S-03: the Beranda previews compose without error, so the mockup seed stays valid for the design tools
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class HomeScreenPreviewsUiTest : HomeComposeTestBase() {
    // S-03: the light and dark success, hidden and empty previews
    @Test
    fun givenSuccessPreviewsWhenComposedThenNoErrorIsRaised() {
        composeRule.setContent {
            HomeSuccessLightPreview()
            HomeSuccessDarkPreview()
            HomeHiddenLightPreview()
            HomeEmptyLightPreview()
            HomeEmptyDarkPreview()
        }
        composeRule.waitForIdle()
    }

    // S-03: the drawer previews and the font scale variant
    @Test
    fun givenDrawerPreviewsWhenComposedThenNoErrorIsRaised() {
        composeRule.setContent {
            HomeSummaryDrawerDarkPreview()
            HomeCategoriesDrawerLightPreview()
            HomeFontScalePreview()
        }
        composeRule.waitForIdle()
    }
}
