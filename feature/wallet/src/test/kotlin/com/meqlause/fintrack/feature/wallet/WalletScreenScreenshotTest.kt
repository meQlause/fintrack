package com.meqlause.fintrack.feature.wallet

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-010..016, S-09: screenshot references of the Dompet previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class WalletScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenSuccessStateInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { WalletSuccessLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/WalletSuccess_light.png")
    }

    @Test
    fun givenSuccessStateInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { WalletSuccessDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/WalletSuccess_dark.png")
    }

    @Test
    fun givenEmptyStateInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { WalletEmptyLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/WalletEmpty_light.png")
    }

    @Test
    fun givenEmptyStateInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { WalletEmptyDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/WalletEmpty_dark.png")
    }

    @Test
    fun givenEditorOpenInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { WalletEditorLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/WalletEditor_light.png")
    }

    @Test
    fun givenEditorOpenInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { WalletEditorDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/WalletEditor_dark.png")
    }
}
