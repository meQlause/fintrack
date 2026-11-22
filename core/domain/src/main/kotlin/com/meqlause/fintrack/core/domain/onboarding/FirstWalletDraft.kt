package com.meqlause.fintrack.core.domain.onboarding

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletType

/** FR-002: the first wallet as the user enters it on the onboarding wallet step. */
public data class FirstWalletDraft(
    public val name: String,
    public val type: WalletType,
    public val initialBalance: Money,
)
