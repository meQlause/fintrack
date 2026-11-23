package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.domain.transaction.TransactionError
import com.meqlause.fintrack.feature.transaction.R

// FR-030..FR-032, S-04: the Indonesian validation message of each rule a draft can break
internal fun TransactionError.messageRes(): Int =
    when (this) {
        TransactionError.AMOUNT_REQUIRED -> R.string.transaction_error_amount_required
        TransactionError.AMOUNT_TOO_LARGE -> R.string.transaction_error_amount_too_large
        TransactionError.FEE_INVALID -> R.string.transaction_error_fee_invalid
        TransactionError.WALLET_REQUIRED -> R.string.transaction_error_wallet_required
        TransactionError.TARGET_WALLET_REQUIRED -> R.string.transaction_error_target_wallet_required
        TransactionError.CATEGORY_REQUIRED -> R.string.transaction_error_category_required
        TransactionError.CATEGORY_KIND_MISMATCH -> R.string.transaction_error_category_kind_mismatch
        TransactionError.SAME_WALLET -> R.string.transaction_error_same_wallet
        TransactionError.CREDIT_SOURCE_NOT_ALLOWED -> R.string.transaction_error_credit_source
        TransactionError.DATE_TOO_FAR -> R.string.transaction_error_date_too_far
        TransactionError.PERSON_REQUIRED -> R.string.transaction_error_person_required
    }
