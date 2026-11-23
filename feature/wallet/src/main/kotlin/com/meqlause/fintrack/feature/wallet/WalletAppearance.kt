package com.meqlause.fintrack.feature.wallet

import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.WalletType

// FR-010: the icon and colour a new wallet gets from its type; the icon and colour pickers come later
internal object WalletAppearance {
    // FR-010: same icons and colours as the onboarding wallet step, neutral for the other types
    fun of(type: WalletType): Appearance =
        when (type) {
            WalletType.CASH -> Appearance(iconKey = "cash", colorKey = "green")
            WalletType.BANK -> Appearance(iconKey = "bank", colorKey = "blue")
            WalletType.EWALLET -> Appearance(iconKey = "ewallet", colorKey = "teal")
            WalletType.CREDIT_CARD -> Appearance(iconKey = "card", colorKey = "slate")
            WalletType.PAYLATER -> Appearance(iconKey = "clock", colorKey = "orange")
            WalletType.INVESTMENT -> Appearance(iconKey = "trend", colorKey = "gray")
            WalletType.OTHER -> Appearance(iconKey = "wallet", colorKey = "gray")
        }
}
