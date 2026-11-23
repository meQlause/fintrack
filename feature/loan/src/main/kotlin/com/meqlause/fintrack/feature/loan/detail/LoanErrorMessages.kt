package com.meqlause.fintrack.feature.loan.detail

import com.meqlause.fintrack.core.domain.loan.RepaymentError
import com.meqlause.fintrack.feature.loan.R

// FR-152: the Indonesian message of each rule a repayment can break
internal fun RepaymentError.messageRes(): Int =
    when (this) {
        RepaymentError.AMOUNT_REQUIRED -> R.string.loan_repay_amount_required
        RepaymentError.AMOUNT_ABOVE_OUTSTANDING -> R.string.loan_repay_above_outstanding
        RepaymentError.WALLET_REQUIRED -> R.string.loan_repay_wallet_required
        RepaymentError.LOAN_CLOSED -> R.string.loan_repay_closed
        RepaymentError.LOAN_NOT_FOUND -> R.string.loan_repay_not_found
    }
