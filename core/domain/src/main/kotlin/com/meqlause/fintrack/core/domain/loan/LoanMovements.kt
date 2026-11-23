package com.meqlause.fintrack.core.domain.loan

import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.TransactionType

// ADR-016: LENT opens with LEND and is repaid by COLLECT; BORROWED opens with BORROW and is repaid by REPAY
internal fun LoanDirection.openingType(): TransactionType =
    when (this) {
        LoanDirection.LENT -> TransactionType.LEND
        LoanDirection.BORROWED -> TransactionType.BORROW
    }

internal fun LoanDirection.repaymentType(): TransactionType =
    when (this) {
        LoanDirection.LENT -> TransactionType.COLLECT
        LoanDirection.BORROWED -> TransactionType.REPAY
    }

// BR-041: only these movements reduce what is still owed
internal fun TransactionType.isRepayment(): Boolean = this == TransactionType.COLLECT || this == TransactionType.REPAY
