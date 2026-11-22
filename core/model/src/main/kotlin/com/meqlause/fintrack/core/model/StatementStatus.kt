package com.meqlause.fintrack.core.model

/** DOC-02: computed, never stored, status of a credit statement (BR-034). */
public enum class StatementStatus {
    NOT_ISSUED,
    DUE,
    PAID,
    OVERDUE,
}
