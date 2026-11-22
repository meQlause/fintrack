package com.meqlause.fintrack.feature.onboarding

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
class OnboardingScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // S-01, FR-001..FR-004: every onboarding state is screenshot-tested in light and dark (DOC-06, DOC-08)
    @Test
    fun `given welcome state in light theme when rendered then it matches the reference (FR-001)`() {
        composeRule.setContent { OnboardingWelcomeLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OnboardingWelcome_light.png")
    }

    @Test
    fun `given welcome state in dark theme when rendered then it matches the reference (FR-001)`() {
        composeRule.setContent { OnboardingWelcomeDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OnboardingWelcome_dark.png")
    }

    @Test
    fun `given wallet state in light theme when rendered then it matches the reference (FR-002)`() {
        composeRule.setContent { OnboardingWalletLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OnboardingWallet_light.png")
    }

    @Test
    fun `given wallet state in dark theme when rendered then it matches the reference (FR-002)`() {
        composeRule.setContent { OnboardingWalletDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OnboardingWallet_dark.png")
    }

    @Test
    fun `given wallet error state in light theme when rendered then it matches the reference (FR-002)`() {
        composeRule.setContent { OnboardingWalletErrorLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OnboardingWalletError_light.png")
    }

    @Test
    fun `given wallet error state in dark theme when rendered then it matches the reference (FR-002)`() {
        composeRule.setContent { OnboardingWalletErrorDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OnboardingWalletError_dark.png")
    }

    @Test
    fun `given notification off state in light theme when rendered then it matches the reference (FR-003)`() {
        composeRule.setContent { OnboardingNotificationOffLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OnboardingNotificationOff_light.png")
    }

    @Test
    fun `given notification off state in dark theme when rendered then it matches the reference (FR-003)`() {
        composeRule.setContent { OnboardingNotificationOffDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OnboardingNotificationOff_dark.png")
    }

    @Test
    fun `given notification on state in light theme when rendered then it matches the reference (FR-003)`() {
        composeRule.setContent { OnboardingNotificationOnLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OnboardingNotificationOn_light.png")
    }

    @Test
    fun `given notification on state in dark theme when rendered then it matches the reference (FR-003)`() {
        composeRule.setContent { OnboardingNotificationOnDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OnboardingNotificationOn_dark.png")
    }

    @Test
    fun `given security state in light theme when rendered then it matches the reference (FR-003)`() {
        composeRule.setContent { OnboardingSecurityLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OnboardingSecurity_light.png")
    }

    @Test
    fun `given security state in dark theme when rendered then it matches the reference (FR-003)`() {
        composeRule.setContent { OnboardingSecurityDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OnboardingSecurity_dark.png")
    }

    @Test
    fun `given security saving state in light theme when rendered then it matches the reference (FR-004)`() {
        composeRule.setContent { OnboardingSecuritySavingLightPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OnboardingSecuritySaving_light.png")
    }

    @Test
    fun `given security saving state in dark theme when rendered then it matches the reference (FR-004)`() {
        composeRule.setContent { OnboardingSecuritySavingDarkPreview() }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/OnboardingSecuritySaving_dark.png")
    }
}
