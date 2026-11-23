package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.model.Security
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

/** FR-102: decides whether the app shows the lock screen when it comes back to the foreground. */
public class ShouldLockUseCase
    @Inject
    constructor() {
        /** FR-102: true when a PIN exists and the app was in the background for at least the lock timeout. */
        public operator fun invoke(
            security: Security,
            backgroundedAt: Instant,
            instant: Instant,
        ): Boolean {
            if (security.pinLock.pinHash == null) return false
            val elapsedSeconds = Duration.between(backgroundedAt, instant).seconds
            return elapsedSeconds >= security.lockTimeoutSeconds
        }
    }
