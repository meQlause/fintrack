package com.meqlause.fintrack.core.domain.security

// FR-100, FR-103: the PIN format and the lockout schedule after wrong attempts
public object PinPolicy {
    /** FR-100: a PIN is exactly six digits (Q-008). */
    public const val LENGTH: Int = 6

    /** FR-103: wrong attempts that are free before the first lockout. */
    public const val FREE_ATTEMPTS: Int = 5

    private const val FIRST_LOCK_SECONDS = 30L
    private const val MAX_LOCK_SECONDS = 900L
    private const val MAX_DOUBLINGS = 10

    /** FR-100: true when [digits] has the PIN length and contains digits only. */
    public fun isValid(digits: String): Boolean = digits.length == LENGTH && digits.all { it in '0'..'9' }

    /** FR-103: seconds of lockout after [failures] wrong attempts in a row; 0 while attempts are still free. */
    public fun lockSeconds(failures: Int): Long {
        if (failures < FREE_ATTEMPTS) return 0L
        val doublings = (failures - FREE_ATTEMPTS).coerceAtMost(MAX_DOUBLINGS)
        return (FIRST_LOCK_SECONDS shl doublings).coerceAtMost(MAX_LOCK_SECONDS)
    }
}
