package com.meqlause.fintrack.core.model

import java.time.Instant

/** DOC-02: the PIN, stored only as hash and salt, and its failed-attempt lockout (DOC-10). */
public data class PinLock(
    public val pinHash: String? = null,
    public val pinSalt: String? = null,
    public val failedPinAttempts: Int = 0,
    public val lockedUntil: Instant = Instant.EPOCH,
)
