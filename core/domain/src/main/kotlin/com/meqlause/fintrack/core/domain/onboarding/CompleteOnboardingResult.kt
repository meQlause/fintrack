package com.meqlause.fintrack.core.domain.onboarding

import com.meqlause.fintrack.core.domain.validation.WalletNameError

/** FR-002: outcome of finishing onboarding; a rejected name is an expected result, not an exception. */
public sealed class CompleteOnboardingResult {
    /** FR-001: the wallet, the default categories and the completed flag are stored. */
    public data object Completed : CompleteOnboardingResult()

    /** FR-002: nothing was stored because the wallet name is not valid. */
    public data class NameRejected(
        public val reason: WalletNameError,
    ) : CompleteOnboardingResult()
}
