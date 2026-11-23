package com.meqlause.fintrack.feature.credit.detail

import com.meqlause.fintrack.core.model.WalletId

// FR-131 to FR-138: every user action of S-14 and S-15, passed to the stateless screen as plain lambdas
internal data class CreditDetailCallbacks(
    val onBack: () -> Unit = {},
    val onOpenPay: () -> Unit = {},
    val onDismissPay: () -> Unit = {},
    val onAmountChange: (String) -> Unit = {},
    val onPickSource: (WalletId) -> Unit = {},
    val onFillRemaining: () -> Unit = {},
    val onClearAmount: () -> Unit = {},
    val onPay: () -> Unit = {},
    val onOpenPicker: (CreditDay) -> Unit = {},
    val onDismissPicker: () -> Unit = {},
    val onPickDay: (Int) -> Unit = {},
)
