package com.meqlause.fintrack.feature.loan.detail

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId

// FR-152: the repayment sheet as the user fills it; the amount stays digits until it is recorded
internal data class RepaymentForm(
    val amountDigits: String = "",
    val walletId: WalletId? = null,
) {
    val amount: Money
        get() = Money(amountDigits.toLongOrNull() ?: 0L)
}
