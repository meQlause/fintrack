package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.security.PinHasher
import com.meqlause.fintrack.core.domain.security.PinPolicy
import com.meqlause.fintrack.core.model.PinLock
import javax.inject.Inject

/** FR-100, DOC-10: stores a new PIN as salt and hash only; the caller checks the current PIN before a change. */
public class SetPinUseCase
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
        private val hasher: PinHasher,
    ) {
        /** FR-100: stores [digits] and clears the failure count; returns false and stores nothing when invalid. */
        public suspend operator fun invoke(digits: String): Boolean {
            if (!PinPolicy.isValid(digits)) return false
            val salt = hasher.newSalt()
            val hash = hasher.hash(digits, salt)
            settingsRepository.update { settings ->
                settings.copy(security = settings.security.copy(pinLock = PinLock(pinHash = hash, pinSalt = salt)))
            }
            return true
        }
    }
