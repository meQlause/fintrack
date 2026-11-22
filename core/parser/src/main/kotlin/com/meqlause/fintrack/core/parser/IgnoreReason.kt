package com.meqlause.fintrack.core.parser

/** FR-085, DOC-07: why a notification is not a transaction. Declaration order is the check order. */
public enum class IgnoreReason {
    OTP,
    PROMOTION,
    BALANCE_INFO,
    FAILED_TRANSACTION,
    PENDING_TRANSACTION,
    LOGIN_ALERT,
}
