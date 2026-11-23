package com.meqlause.fintrack.feature.wallet.detail

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// FR-017, S-19: screenshot references of the wallet detail previews in light and dark themes
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w390dp-h844dp")
class WalletDetailScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun givenCollapsedDetailInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { WalletDetailLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/WalletDetail_light.png")
    }

    @Test
    fun givenCollapsedDetailInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { WalletDetailDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/WalletDetail_dark.png")
    }

    @Test
    fun givenExpandedDetailInLightThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { WalletDetailExpandedLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/WalletDetailExpanded_light.png")
    }

    @Test
    fun givenExpandedDetailInDarkThemeWhenRenderedThenItMatchesReference() {
        composeRule.setContent { WalletDetailExpandedDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/WalletDetailExpanded_dark.png")
    }
}
