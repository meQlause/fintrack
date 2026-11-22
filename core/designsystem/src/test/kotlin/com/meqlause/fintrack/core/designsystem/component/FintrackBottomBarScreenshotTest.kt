package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class FintrackBottomBarScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // DOC-09: the liquid bar is screenshot-tested for each selected tab, in light and dark
    @Test
    fun `given beranda selected in light theme when rendered then the bar matches the reference (DOC-09)`() {
        composeRule.setContent { BottomBarHomeLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/FintrackBottomBar_home_light.png")
    }

    @Test
    fun `given beranda selected in dark theme when rendered then the bar matches the reference (DOC-09)`() {
        composeRule.setContent { BottomBarHomeDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/FintrackBottomBar_home_dark.png")
    }

    @Test
    fun `given riwayat selected in light theme when rendered then the bar matches the reference (DOC-09)`() {
        composeRule.setContent { BottomBarHistoryLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/FintrackBottomBar_history_light.png")
    }

    @Test
    fun `given riwayat selected in dark theme when rendered then the bar matches the reference (DOC-09)`() {
        composeRule.setContent { BottomBarHistoryDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/FintrackBottomBar_history_dark.png")
    }

    @Test
    fun `given investasi selected in light theme when rendered then the bar matches the reference (DOC-09)`() {
        composeRule.setContent { BottomBarInvestmentLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/FintrackBottomBar_investment_light.png")
    }

    @Test
    fun `given investasi selected in dark theme when rendered then the bar matches the reference (DOC-09)`() {
        composeRule.setContent { BottomBarInvestmentDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/FintrackBottomBar_investment_dark.png")
    }

    @Test
    fun `given dompet selected in light theme when rendered then the bar matches the reference (DOC-09)`() {
        composeRule.setContent { BottomBarWalletLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/FintrackBottomBar_wallet_light.png")
    }

    @Test
    fun `given dompet selected in dark theme when rendered then the bar matches the reference (DOC-09)`() {
        composeRule.setContent { BottomBarWalletDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/FintrackBottomBar_wallet_dark.png")
    }
}
