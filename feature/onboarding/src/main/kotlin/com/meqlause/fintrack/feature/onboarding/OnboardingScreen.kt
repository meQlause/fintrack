package com.meqlause.fintrack.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.theme.MotionToken

/**
 * S-01, FR-001..FR-004: stateless onboarding. Every page is drawn from [state]; user actions leave through the
 * callbacks, so the screen can be previewed and screenshot-tested for each state.
 */
@Composable
internal fun OnboardingScreen(
    state: OnboardingUiState,
    onAction: (OnboardingAction) -> Unit,
    onEnableNotifications: () -> Unit,
    onSetupPin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = FintrackTheme.spacing
    val motion = FintrackTheme.motion.medium
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(FintrackTheme.colors.surfaces.background)
                .padding(horizontal = spacing.large, vertical = spacing.large),
    ) {
        SkipAction(
            visible = state == OnboardingUiState.Welcome,
            onSkip = { onAction(OnboardingAction.SkipIntro) },
        )
        AnimatedContent(
            targetState = state,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            transitionSpec = { pageTransition(motion) },
            contentKey = { it.page },
            label = "onboardingPage",
        ) { current ->
            OnboardingPageContent(current = current, onAction = onAction)
        }
        PageDots(current = state.page.ordinal, count = OnboardingPage.entries.size)
        OnboardingActions(
            state = state,
            onAction = onAction,
            onEnableNotifications = onEnableNotifications,
            onSetupPin = onSetupPin,
        )
    }
}

@Composable
private fun OnboardingPageContent(
    current: OnboardingUiState,
    onAction: (OnboardingAction) -> Unit,
) {
    when (current) {
        OnboardingUiState.Welcome -> WelcomePage()
        is OnboardingUiState.FirstWallet -> FirstWalletPage(form = current.form, onAction = onAction)
        is OnboardingUiState.NotificationAccess -> NotificationPage(isGranted = current.isGranted)
        is OnboardingUiState.Security -> SecurityPage()
        OnboardingUiState.Completed -> Unit
    }
}

// S-01: pages slide in the direction of the flow and back when a page is revisited
private fun AnimatedContentTransitionScope<OnboardingUiState>.pageTransition(motion: MotionToken): ContentTransform {
    val isForward = targetState.page.ordinal > initialState.page.ordinal
    val direction = if (isForward) 1 else -1
    val slide = tween<IntOffset>(durationMillis = motion.durationMillis, easing = motion.easing)
    val fade = tween<Float>(durationMillis = motion.durationMillis, easing = motion.easing)
    val enter = slideInHorizontally(animationSpec = slide) { fullWidth -> direction * fullWidth } + fadeIn(fade)
    val exit = slideOutHorizontally(animationSpec = slide) { fullWidth -> -direction * fullWidth } + fadeOut(fade)
    return enter togetherWith exit
}
