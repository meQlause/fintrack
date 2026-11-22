package com.meqlause.fintrack.core.domain.validation

import javax.inject.Inject

/** DOC-02, FR-002: a wallet name is 1 to 30 characters after trimming spaces at both ends. */
public class WalletNameValidator
    @Inject
    constructor() {
        /** DOC-02: the reason [name] is rejected, or null when it is accepted. */
        public fun validate(name: String): WalletNameError? {
            val trimmed = name.trim()
            return when {
                trimmed.isEmpty() -> WalletNameError.BLANK
                trimmed.length > MAX_LENGTH -> WalletNameError.TOO_LONG
                else -> null
            }
        }

        private companion object {
            const val MAX_LENGTH = 30
        }
    }
