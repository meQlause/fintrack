package com.meqlause.fintrack.feature.onboarding

import com.meqlause.fintrack.core.model.WalletType

/** S-01, FR-001..FR-003: user actions the ViewModel handles. Opening settings and the PIN flow are route work. */
internal sealed interface OnboardingAction {
    /** FR-001: "Lewati" on the welcome page; the wallet step is required, so it comes next. */
    data object SkipIntro : OnboardingAction

    /** FR-001: "Lanjut", "Nanti saja" and the notification page's continue; validates the wallet step. */
    data object Continue : OnboardingAction

    /** FR-001, FR-004: "Mulai" on the last page; stores the wallet, the categories and the completed flag. */
    data object Finish : OnboardingAction

    /** FR-002: the keypad backspace key. */
    data object BackspaceTyped : OnboardingAction

    /** FR-002: the wallet name field changed. */
    data class NameChanged(
        val name: String,
    ) : OnboardingAction

    /** FR-002: one of the wallet type chips was tapped. */
    data class TypeSelected(
        val type: WalletType,
    ) : OnboardingAction

    /** FR-002: digit keys of the keypad; "000" appends three zeros. */
    data class DigitTyped(
        val digits: String,
    ) : OnboardingAction
}
