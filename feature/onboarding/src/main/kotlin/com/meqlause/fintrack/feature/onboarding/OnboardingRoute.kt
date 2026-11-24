package com.meqlause.fintrack.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meqlause.fintrack.core.ui.notification.NotificationAccessGate

/**
 * S-01, FR-001..FR-004: stateful entry of onboarding. The only composable that touches the ViewModel.
 * Re-checks notification access each time the screen resumes, which is when the user returns from settings.
 */
@Composable
internal fun OnboardingRoute(
    onFinished: () -> Unit,
    onSetupPin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentOnFinished by rememberUpdatedState(onFinished)
    LifecycleResumeEffect(Unit) {
        viewModel.onNotificationAccessChecked(NotificationAccess.isGranted(context))
        onPauseOrDispose {}
    }
    LaunchedEffect(state) {
        if (state == OnboardingUiState.Completed) currentOnFinished()
    }
    // DOC-10: "Aktifkan" shows the disclosure first; only "Setuju" opens the system settings
    NotificationAccessGate(modifier = modifier) { requestAccess ->
        OnboardingScreen(
            state = state,
            callbacks =
                OnboardingCallbacks(
                    onAction = viewModel::onAction,
                    onEnableNotifications = requestAccess,
                    onSetupPin = onSetupPin,
                ),
        )
    }
}
