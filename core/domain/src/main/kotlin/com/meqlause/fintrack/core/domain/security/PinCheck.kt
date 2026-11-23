package com.meqlause.fintrack.core.domain.security

import java.time.Instant

/** FR-100, FR-103: outcome of checking a PIN; a wrong PIN is an expected result, not an exception. */
public sealed interface PinCheck {
    /** FR-100: the PIN matches; the failure count is reset. */
    public data object Accepted : PinCheck

    /** FR-100: the PIN is wrong and the lockout has not started yet. */
    public data object Rejected : PinCheck

    /** FR-103: the PIN cannot be tried before [until]. */
    public data class LockedOut(
        public val until: Instant,
    ) : PinCheck
}
