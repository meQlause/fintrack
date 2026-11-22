package com.meqlause.fintrack.core.model

/** DOC-02: type of money movement; loan types follow ADR-016 and BR-040. */
public enum class TransactionType {
    EXPENSE,
    INCOME,
    TRANSFER,
    LEND,
    BORROW,
    COLLECT,
    REPAY,
}
