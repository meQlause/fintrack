package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.domain.transaction.TransactionError
import com.meqlause.fintrack.feature.transaction.R

// FR-030..FR-032, S-04: the Indonesian validation message of each rule a draft can break
private val MESSAGES: Map<TransactionError, Int> =
    mapOf(
        TransactionError.AMOUNT_REQUIRED to R.string.transaction_error_amount_required,
        TransactionError.AMOUNT_TOO_LARGE to R.string.transaction_error_amount_too_large,
        TransactionError.FEE_INVALID to R.string.transaction_error_fee_invalid,
        TransactionError.WALLET_REQUIRED to R.string.transaction_error_wallet_required,
        TransactionError.TARGET_WALLET_REQUIRED to R.string.transaction_error_target_wallet_required,
        TransactionError.CATEGORY_REQUIRED to R.string.transaction_error_category_required,
        TransactionError.CATEGORY_KIND_MISMATCH to R.string.transaction_error_category_kind_mismatch,
        TransactionError.SAME_WALLET to R.string.transaction_error_same_wallet,
        TransactionError.CREDIT_SOURCE_NOT_ALLOWED to R.string.transaction_error_credit_source,
        TransactionError.DATE_TOO_FAR to R.string.transaction_error_date_too_far,
        TransactionError.PERSON_REQUIRED to R.string.transaction_error_person_required,
    )

// FR-030..FR-032, S-04: the message of one rule
internal fun TransactionError.messageRes(): Int = MESSAGES.getValue(this)
