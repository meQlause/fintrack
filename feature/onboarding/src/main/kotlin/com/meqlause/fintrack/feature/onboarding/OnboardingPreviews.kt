package com.meqlause.fintrack.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.validation.WalletNameError

// S-01: preview and screenshot host for one onboarding state; actions are no-ops
@Composable
internal fun OnboardingPreviewScreen(
    state: OnboardingUiState,
    darkTheme: Boolean,
) {
    FintrackTheme(darkTheme = darkTheme) {
        OnboardingScreen(
            state = state,
            onAction = {},
            onEnableNotifications = {},
            onSetupPin = {},
        )
    }
}

// S-01: the wallet step with a typed balance, used by the wallet previews
internal fun walletFormSample(): FirstWalletForm = FirstWalletForm.initial().withDigits("150000")

// S-01, FR-002: the wallet step after "Lanjut" with a blank name
internal fun walletFormBlankNameSample(): FirstWalletForm =
    FirstWalletForm.initial().copy(name = "", nameError = WalletNameError.BLANK)

@Preview(name = "Onboarding welcome light", showBackground = true)
@Composable
internal fun OnboardingWelcomeLightPreview() = OnboardingPreviewScreen(OnboardingUiState.Welcome, darkTheme = false)

@Preview(name = "Onboarding wallet light", showBackground = true)
@Composable
internal fun OnboardingWalletLightPreview() =
    OnboardingPreviewScreen(OnboardingUiState.FirstWallet(walletFormSample()), darkTheme = false)

@Preview(name = "Onboarding wallet error light", showBackground = true)
@Composable
internal fun OnboardingWalletErrorLightPreview() =
    OnboardingPreviewScreen(OnboardingUiState.FirstWallet(walletFormBlankNameSample()), darkTheme = false)

@Preview(name = "Onboarding notification off light", showBackground = true)
@Composable
internal fun OnboardingNotificationOffLightPreview() =
    OnboardingPreviewScreen(OnboardingUiState.NotificationAccess(isGranted = false), darkTheme = false)

@Preview(name = "Onboarding notification on light", showBackground = true)
@Composable
internal fun OnboardingNotificationOnLightPreview() =
    OnboardingPreviewScreen(OnboardingUiState.NotificationAccess(isGranted = true), darkTheme = false)

@Preview(name = "Onboarding security light", showBackground = true)
@Composable
internal fun OnboardingSecurityLightPreview() =
    OnboardingPreviewScreen(OnboardingUiState.Security(isSaving = false), darkTheme = false)

@Preview(name = "Onboarding security saving light", showBackground = true)
@Composable
internal fun OnboardingSecuritySavingLightPreview() =
    OnboardingPreviewScreen(OnboardingUiState.Security(isSaving = true), darkTheme = false)
