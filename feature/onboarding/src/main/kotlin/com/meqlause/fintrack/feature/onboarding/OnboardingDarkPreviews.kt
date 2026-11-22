package com.meqlause.fintrack.feature.onboarding

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

// S-01: dark-theme previews, one per onboarding state

@Preview(name = "Onboarding welcome dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun OnboardingWelcomeDarkPreview() = OnboardingPreviewScreen(OnboardingUiState.Welcome, darkTheme = true)

@Preview(name = "Onboarding wallet dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun OnboardingWalletDarkPreview() =
    OnboardingPreviewScreen(OnboardingUiState.FirstWallet(walletFormSample()), darkTheme = true)

@Preview(name = "Onboarding wallet error dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun OnboardingWalletErrorDarkPreview() =
    OnboardingPreviewScreen(OnboardingUiState.FirstWallet(walletFormBlankNameSample()), darkTheme = true)

@Preview(name = "Onboarding notification off dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun OnboardingNotificationOffDarkPreview() =
    OnboardingPreviewScreen(OnboardingUiState.NotificationAccess(isGranted = false), darkTheme = true)

@Preview(name = "Onboarding notification on dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun OnboardingNotificationOnDarkPreview() =
    OnboardingPreviewScreen(OnboardingUiState.NotificationAccess(isGranted = true), darkTheme = true)

@Preview(name = "Onboarding security dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun OnboardingSecurityDarkPreview() =
    OnboardingPreviewScreen(OnboardingUiState.Security(isSaving = false), darkTheme = true)

@Preview(name = "Onboarding security saving dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun OnboardingSecuritySavingDarkPreview() =
    OnboardingPreviewScreen(OnboardingUiState.Security(isSaving = true), darkTheme = true)
