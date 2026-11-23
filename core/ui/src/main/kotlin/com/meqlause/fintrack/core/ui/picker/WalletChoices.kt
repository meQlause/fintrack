package com.meqlause.fintrack.core.ui.picker

// S-18: the wallet rows of the picker and which wallet kinds it may offer
data class WalletChoices(
    val rows: List<WalletPickerRow>,
    val allowCredit: Boolean = true,
    val allowInvestment: Boolean = false,
)
