package com.meqlause.fintrack.feature.credit.detail

import com.meqlause.fintrack.core.model.WalletId

// FR-131 to FR-138: every user action of S-14 and S-15, passed to the stateless screen as plain lambdas
internal data class CreditDetailCallbacks(
    val onBack: () -> Unit = {},
    val bill: Bill = Bill(),
    val entry: Entry = Entry(),
    val days: Days = Days(),
    val plans: Plans = Plans(),
) {
    val onOpenPay: () -> Unit get() = bill.onOpenPay
    val onDismissPay: () -> Unit get() = bill.onDismissPay
    val onPay: () -> Unit get() = bill.onPay
    val onAmountChange: (String) -> Unit get() = entry.onAmountChange
    val onPickSource: (WalletId) -> Unit get() = entry.onPickSource
    val onFillRemaining: () -> Unit get() = entry.onFillRemaining
    val onClearEntry: () -> Unit get() = entry.onClearEntry
    val onOpenPicker: (CreditDay) -> Unit get() = days.onOpenPicker
    val onDismissPicker: () -> Unit get() = days.onDismissPicker
    val onPickDay: (Int) -> Unit get() = days.onPickDay
    val onOpenInstalment: (Long) -> Unit get() = plans.onOpenInstalment
    val onOpenInstalments: () -> Unit get() = plans.onOpenInstalments

    // S-15: opening, dismissing and confirming the bill payment sheet
    internal data class Bill(
        val onOpenPay: () -> Unit = {},
        val onDismissPay: () -> Unit = {},
        val onPay: () -> Unit = {},
    )

    // S-15: the amount typed, the source wallet and the two quick fills
    internal data class Entry(
        val onAmountChange: (String) -> Unit = {},
        val onPickSource: (WalletId) -> Unit = {},
        val onFillRemaining: () -> Unit = {},
        val onClearEntry: () -> Unit = {},
    )

    // FR-138: the statement and due day drawer
    internal data class Days(
        val onOpenPicker: (CreditDay) -> Unit = {},
        val onDismissPicker: () -> Unit = {},
        val onPickDay: (Int) -> Unit = {},
    )

    // FR-143: the instalment plans of the card
    internal data class Plans(
        val onOpenInstalment: (Long) -> Unit = {},
        val onOpenInstalments: () -> Unit = {},
    )
}
