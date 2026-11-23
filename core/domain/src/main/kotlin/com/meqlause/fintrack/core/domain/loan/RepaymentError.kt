package com.meqlause.fintrack.core.domain.loan

// FR-152: why a repayment cannot be recorded; the feature maps each value to one Indonesian message
public enum class RepaymentError {
    /** FR-152: the amount is zero or above the transaction bound. */
    AMOUNT_REQUIRED,

    /** BR-041: the amount is above what is still outstanding ("Melebihi sisa pinjaman"). */
    AMOUNT_ABOVE_OUTSTANDING,

    /** FR-152: no wallet was chosen for the money. */
    WALLET_REQUIRED,

    /** BR-042: the loan is already settled or written off. */
    LOAN_CLOSED,

    /** FR-153: the loan id does not exist. */
    LOAN_NOT_FOUND,
}
