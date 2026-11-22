package com.meqlause.fintrack.feature.onboarding

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import assertk.assertThat
import assertk.assertions.containsExactly
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h720dp")
class OnboardingInteractionTest {
    @get:Rule
    val composeRule = createComposeRule()

    // FR-001 AC: "Lewati" on the welcome page sends the skip action, which moves on to the required wallet step
    @Test
    fun `given welcome page when skip is tapped then the skip action is sent (FR-001)`() {
        val actions = mutableListOf<OnboardingAction>()
        setScreen(OnboardingUiState.Welcome, actions)

        composeRule.onNodeWithText("Lewati").performClick()

        assertThat(actions).containsExactly(OnboardingAction.SkipIntro)
    }

    // FR-002: the keypad digits are sent as typed, so the balance follows the keypad
    @Test
    fun `given wallet page when a keypad digit is tapped then the digit action is sent (FR-002)`() {
        val actions = mutableListOf<OnboardingAction>()
        setScreen(OnboardingUiState.FirstWallet(FirstWalletForm.initial()), actions)

        composeRule.onNodeWithText("7").performClick()

        assertThat(actions).containsExactly(OnboardingAction.DigitTyped("7"))
    }

    private fun setScreen(
        state: OnboardingUiState,
        actions: MutableList<OnboardingAction>,
    ) {
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                OnboardingScreen(
                    state = state,
                    onAction = { actions.add(it) },
                    onEnableNotifications = {},
                    onSetupPin = {},
                )
            }
        }
    }
}
