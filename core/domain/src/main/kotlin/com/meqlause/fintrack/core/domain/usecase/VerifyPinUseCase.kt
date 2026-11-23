package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.security.PinCheck
import com.meqlause.fintrack.core.domain.security.PinHasher
import com.meqlause.fintrack.core.domain.security.PinPolicy
import com.meqlause.fintrack.core.model.PinLock
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

/**
 * FR-100, FR-103: checks an entered PIN. Wrong entries are counted in a row and the failures are stored,
 * so killing the app does not reset them. While locked out, entries are refused without being counted.
 */
public class VerifyPinUseCase
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
        private val hasher: PinHasher,
        private val clock: Clock,
    ) {
        /** FR-100: accepted when no PIN is set or [digits] is the PIN; a lockout starts after five wrong tries. */
        public suspend operator fun invoke(digits: String): PinCheck {
            val pinLock =
                settingsRepository
                    .observe()
                    .first()
                    .security.pinLock
            val instant = clock.instant()
            val hash = pinLock.pinHash
            val salt = pinLock.pinSalt
            return when {
                hash == null || salt == null -> {
                    PinCheck.Accepted
                }

                instant < pinLock.lockedUntil -> {
                    PinCheck.LockedOut(pinLock.lockedUntil)
                }

                hasher.matches(digits, salt, hash) -> {
                    store(pinLock.copy(failedPinAttempts = 0, lockedUntil = instant))
                    PinCheck.Accepted
                }

                else -> {
                    recordFailure(pinLock, instant)
                }
            }
        }

        private suspend fun recordFailure(
            pinLock: PinLock,
            instant: Instant,
        ): PinCheck {
            val failures = pinLock.failedPinAttempts + 1
            val until = instant.plusSeconds(PinPolicy.lockSeconds(failures))
            store(pinLock.copy(failedPinAttempts = failures, lockedUntil = until))
            return if (until > instant) PinCheck.LockedOut(until) else PinCheck.Rejected
        }

        private suspend fun store(pinLock: PinLock) {
            settingsRepository.update { settings ->
                settings.copy(security = settings.security.copy(pinLock = pinLock))
            }
        }
    }
