package com.meqlause.fintrack.core.domain.quickentry

/** FR-039, BR-047: every rule a shortcut can break. */
public enum class QuickEntryError {
    TITLE_REQUIRED,
    TITLE_TOO_LONG,
    NOTE_TOO_LONG,
    TYPE_NOT_SHORTCUT,
    AMOUNT_INVALID,
    CATEGORY_KIND_MISMATCH,
    WALLET_NOT_ALLOWED,
}
