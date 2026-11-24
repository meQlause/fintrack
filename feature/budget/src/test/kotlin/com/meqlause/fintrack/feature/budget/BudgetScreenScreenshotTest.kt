package com.meqlause.fintrack.feature.budget

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

// FR-070..074, S-11: screenshot references of the Anggaran previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class BudgetScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `given success state in light theme when rendered then it matches the reference (S-11)`() {
        composeRule.setContent { BudgetSuccessLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/BudgetSuccess_light.png")
    }

    @Test
    fun `given success state in dark theme when rendered then it matches the reference (S-11)`() {
        composeRule.setContent { BudgetSuccessDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/BudgetSuccess_dark.png")
    }

    @Test
    fun `given empty state in light theme when rendered then it matches the reference (S-11)`() {
        composeRule.setContent { BudgetEmptyLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/BudgetEmpty_light.png")
    }

    @Test
    fun `given empty state in dark theme when rendered then it matches the reference (S-11)`() {
        composeRule.setContent { BudgetEmptyDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/BudgetEmpty_dark.png")
    }

    // NFR-010: font scale 2.0 variant
    @Test
    fun `given font scale 2 when rendered then the budget screen matches the reference (NFR-010)`() {
        composeRule.setContent { WithFontScaleTwo { BudgetFontScalePreview() } }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/BudgetSuccess_fontScale2.png")
    }

    @Composable
    private fun WithFontScaleTwo(content: @Composable () -> Unit) {
        val density = LocalDensity.current.density
        CompositionLocalProvider(LocalDensity provides Density(density = density, fontScale = 2f)) {
            content()
        }
    }
}
