package com.meqlause.fintrack.feature.dashboard

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-120: with the system in night mode the header offers the sun, and its tap reports the dark state
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp-night")
class HomeHeaderThemeUiTest : HomeComposeTestBase() {
    // FR-120: the theme button in night mode reports that the screen is dark
    @Test
    fun givenNightModeWhenThemeTappedThenCallbackReportsDark() {
        var reported: Boolean? = null
        val callbacks = HomeCallbacks(screen = HomeCallbacks.Screen(onToggleTheme = { reported = it }))
        setHome(HomeFixtures.rich(), callbacks)

        composeRule.onNodeWithContentDescription(label(R.string.home_theme_description)).performClick()

        assertEquals(true, reported)
    }
}
