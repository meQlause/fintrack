package com.meqlause.fintrack.feature.loan.detail

import com.meqlause.fintrack.core.domain.loan.RepaymentError
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId

// FR-152: the repayment sheet as the user fills it; the amount stays digits until it is recorded
internal data class RepaymentForm(
    val rupiahDigits: String = "",
    val walletId: WalletId? = null,
    val errors: Set<RepaymentError> = emptySet(),
) {
    val amount: Money
        get() = Money(rupiahDigits.toLongOrNull() ?: 0L)
}
