package com.meqlause.fintrack.core.domain.loan

import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId

// FR-152, BR-041, BR-042: the checks a repayment passes before it is stored
internal object RepaymentRules {
    /** Every broken rule; an empty set means the repayment may be stored. */
    fun check(
        amount: Money,
        walletId: WalletId?,
        entry: LoanEntry,
    ): Set<RepaymentError> =
        buildSet {
            if (!amount.isTransactionAmount()) add(RepaymentError.AMOUNT_REQUIRED)
            if (walletId == null) add(RepaymentError.WALLET_REQUIRED)
            if (entry.loan.status != LoanStatus.OPEN) add(RepaymentError.LOAN_CLOSED)
            if (amount > entry.balance.outstanding) add(RepaymentError.AMOUNT_ABOVE_OUTSTANDING)
        }
}
