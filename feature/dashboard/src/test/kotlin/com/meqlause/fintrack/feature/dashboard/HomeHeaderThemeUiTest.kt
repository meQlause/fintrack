package com.meqlause.fintrack.feature.dashboard

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-120: the header toggle follows the theme the app shows, not the night mode of the device
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp-night")
class HomeHeaderThemeUiTest : HomeComposeTestBase() {
    // FR-120: the device is in night mode but the app shows the light theme, so the button reports light
    @Test
    fun givenNightDeviceWithLightAppThemeWhenThemeTappedThenCallbackReportsLight() {
        var reported: Boolean? = null
        val callbacks = HomeCallbacks(screen = HomeCallbacks.Screen(onToggleTheme = { reported = it }))
        setHome(HomeFixtures.rich(), callbacks, darkTheme = false)

        composeRule.onNodeWithContentDescription(label(R.string.home_theme_description)).performClick()

        assertEquals(false, reported)
    }

    // FR-120: the app shows the dark theme, so the button reports dark
    @Test
    fun givenDarkAppThemeWhenThemeTappedThenCallbackReportsDark() {
        var reported: Boolean? = null
        val callbacks = HomeCallbacks(screen = HomeCallbacks.Screen(onToggleTheme = { reported = it }))
        setHome(HomeFixtures.rich(), callbacks, darkTheme = true)

        composeRule.onNodeWithContentDescription(label(R.string.home_theme_description)).performClick()

        assertEquals(true, reported)
    }
}
