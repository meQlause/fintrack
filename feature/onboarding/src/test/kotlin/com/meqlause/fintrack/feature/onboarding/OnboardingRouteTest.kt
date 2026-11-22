package com.meqlause.fintrack.feature.onboarding

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h720dp")
class OnboardingRouteTest {
    @get:Rule
    val composeRule = createComposeRule()

    // FR-001 AC: after onboarding the Dashboard is shown, so the route reports completion once the flow ends
    @Test
    fun `given the onboarding steps tapped to the end when finished then the route reports completion (FR-001)`() {
        var finished = false
        val viewModel = OnboardingFixture().viewModel()
        composeRule.setContent {
            FintrackTheme(darkTheme = false) {
                OnboardingRoute(onFinished = { finished = true }, onSetupPin = {}, viewModel = viewModel)
            }
        }

        composeRule.onNodeWithText("Lewati").performClick()
        composeRule.onNodeWithText("Lanjut").performClick()
        composeRule.onNodeWithText("Nanti saja").performClick()
        composeRule.onNodeWithText("Mulai").performClick()
        composeRule.waitForIdle()

        assertTrue(finished)
    }
}
