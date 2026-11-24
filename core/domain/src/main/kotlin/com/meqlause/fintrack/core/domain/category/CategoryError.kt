package com.meqlause.fintrack.core.domain.category

/** FR-021, DOC-02: every rule a new category can break. */
public enum class CategoryError {
    NAME_REQUIRED,
    NAME_TOO_LONG,
    NAME_TAKEN,
}
