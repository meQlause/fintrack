package com.meqlause.fintrack.core.domain.loan

import com.meqlause.fintrack.core.domain.transaction.TransactionError
import com.meqlause.fintrack.core.model.Money

// FR-150, FR-151: the checks a loan draft passes before anything is stored
internal object LoanRules {
    /** Every broken rule; an empty set means the loan may be stored. */
    fun check(draft: LoanDraft): Set<TransactionError> =
        buildSet {
            val amount = draft.principal.rupiah
            if (amount < Money.MIN_TRANSACTION_RUPIAH) add(TransactionError.AMOUNT_REQUIRED)
            if (amount > Money.MAX_TRANSACTION_RUPIAH) add(TransactionError.AMOUNT_TOO_LARGE)
            if (draft.personName.isBlank()) add(TransactionError.PERSON_REQUIRED)
            if (draft.walletId == null) add(TransactionError.WALLET_REQUIRED)
        }
}
