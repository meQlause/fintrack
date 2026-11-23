package com.meqlause.fintrack.core.domain.transaction

/** FR-030..FR-032: why a transaction draft cannot be stored; the feature maps each value to one Indonesian message. */
public enum class TransactionError {
    /** BR-001: the amount is zero or negative. */
    AMOUNT_REQUIRED,

    /** BR-001: the amount is above 999,999,999,999 rupiah. */
    AMOUNT_TOO_LARGE,

    /** BR-004: a transfer fee is negative or above the transaction amount bound. */
    FEE_INVALID,

    /** BR-002: the transaction has no existing source wallet. */
    WALLET_REQUIRED,

    /** BR-002: a TRANSFER has no target wallet. */
    TARGET_WALLET_REQUIRED,

    /** BR-002: an EXPENSE or INCOME has no category. */
    CATEGORY_REQUIRED,

    /** BR-006: the category kind does not match the transaction type. */
    CATEGORY_KIND_MISMATCH,

    /** BR-003: a TRANSFER uses the same wallet on both sides. */
    SAME_WALLET,

    /** BR-039: a credit wallet can never be the source of a TRANSFER. */
    CREDIT_SOURCE_NOT_ALLOWED,

    /** BR-009: occurredAt is more than one day in the future. */
    DATE_TOO_FAR,
}
