package com.meqlause.fintrack.feature.onboarding

// S-01, FR-001..FR-004: every action of the onboarding screen; defaults do nothing so previews can omit them
internal data class OnboardingCallbacks(
    val onAction: (OnboardingAction) -> Unit = {},
    val onEnableNotifications: () -> Unit = {},
    val onSetupPin: () -> Unit = {},
)
