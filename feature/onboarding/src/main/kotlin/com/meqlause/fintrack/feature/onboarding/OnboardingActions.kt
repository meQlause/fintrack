package com.meqlause.fintrack.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

/** S-01, FR-001..FR-004: the primary button and the secondary action of the current page. */
@Composable
internal fun OnboardingActions(
    state: OnboardingUiState,
    onAction: (OnboardingAction) -> Unit,
    onEnableNotifications: () -> Unit,
    onSetupPin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (state) {
            OnboardingUiState.Welcome, is OnboardingUiState.FirstWallet -> {
                NextButton(onClick = { onAction(OnboardingAction.Continue) })
            }

            is OnboardingUiState.NotificationAccess -> {
                NotificationButtons(
                    isGranted = state.isGranted,
                    onAction = onAction,
                    onEnableNotifications = onEnableNotifications,
                )
            }

            is OnboardingUiState.Security -> {
                SecurityButtons(isSaving = state.isSaving, onAction = onAction, onSetupPin = onSetupPin)
            }

            OnboardingUiState.Completed -> {
                Unit
            }
        }
    }
}

@Composable
private fun NextButton(onClick: () -> Unit) {
    FintrackButton(text = stringResource(R.string.onboarding_next), onClick = onClick)
}

// FR-003: "Aktifkan" opens the system settings; once access is granted the page only continues
@Composable
private fun NotificationButtons(
    isGranted: Boolean,
    onAction: (OnboardingAction) -> Unit,
    onEnableNotifications: () -> Unit,
) {
    if (isGranted) {
        NextButton(onClick = { onAction(OnboardingAction.Continue) })
        return
    }
    FintrackButton(text = stringResource(R.string.onboarding_notification_enable), onClick = onEnableNotifications)
    TextAction(
        label = stringResource(R.string.onboarding_later),
        onClick = { onAction(OnboardingAction.Continue) },
    )
}

// FR-003: "Mulai" completes onboarding without a PIN; the PIN offer is optional
@Composable
private fun SecurityButtons(
    isSaving: Boolean,
    onAction: (OnboardingAction) -> Unit,
    onSetupPin: () -> Unit,
) {
    FintrackButton(
        text = stringResource(R.string.onboarding_start),
        onClick = { onAction(OnboardingAction.Finish) },
        enabled = !isSaving,
    )
    TextAction(label = stringResource(R.string.onboarding_pin_setup), onClick = onSetupPin)
}
