package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-036, FR-037, S-05: screenshot references of the Riwayat previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class HistoryScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenSampleListInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HistoryScreenLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/History_light.png")
    }

    @Test
    fun givenSampleListInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HistoryScreenDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/History_dark.png")
    }

    @Test
    fun givenStatisticsInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HistoryStatisticsLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/HistoryStatistics_light.png")
    }

    @Test
    fun givenStatisticsInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { HistoryStatisticsDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/HistoryStatistics_dark.png")
    }

    // FR-036, NFR-010: font scale 2.0 variant rendered from its own preview
    @Test
    fun `given font scale 2 when rendered then the history screen matches the reference (NFR-010)`() {
        composeRule.setContent { WithFontScaleTwo { HistoryScreenFontScalePreview() } }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/History_fontScale2.png")
    }

    @Composable
    private fun WithFontScaleTwo(content: @Composable () -> Unit) {
        val density = LocalDensity.current.density
        CompositionLocalProvider(LocalDensity provides Density(density = density, fontScale = 2f)) {
            content()
        }
    }
}
