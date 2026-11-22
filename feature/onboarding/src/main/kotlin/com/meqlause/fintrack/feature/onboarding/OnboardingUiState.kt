package com.meqlause.fintrack.feature.onboarding

/**
 * S-01, FR-001..FR-004: what the onboarding screen shows. One state per page; [Completed] leaves the flow
 * and the route navigates to the Dashboard.
 */
internal sealed interface OnboardingUiState {
    /** The page this state belongs to, used for the dot indicator and the slide direction. */
    val page: OnboardingPage

    /** FR-001: page 1, offline and private. */
    data object Welcome : OnboardingUiState {
        override val page: OnboardingPage = OnboardingPage.WELCOME
    }

    /** FR-002: page 2, the required first wallet. */
    data class FirstWallet(
        val form: FirstWalletForm,
    ) : OnboardingUiState {
        override val page: OnboardingPage = OnboardingPage.WALLET
    }

    /** FR-003: page 3, notification detection; [isGranted] is re-checked when the user returns from settings. */
    data class NotificationAccess(
        val isGranted: Boolean,
    ) : OnboardingUiState {
        override val page: OnboardingPage = OnboardingPage.NOTIFICATION
    }

    /** FR-003: page 4, PIN offer; [isSaving] is true while the onboarding data is stored. */
    data class Security(
        val isSaving: Boolean,
    ) : OnboardingUiState {
        override val page: OnboardingPage = OnboardingPage.SECURITY
    }

    /** FR-001: onboarding is stored; the route leaves the flow. */
    data object Completed : OnboardingUiState {
        override val page: OnboardingPage = OnboardingPage.SECURITY
    }
}
