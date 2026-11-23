package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.security.PinCheck
import com.meqlause.fintrack.core.model.PinLock
import javax.inject.Inject

/** FR-100, FR-101: removes the PIN once the current PIN is entered; biometrics are turned off with it. */
public class RemovePinUseCase
    @Inject
    constructor(
        private val verifyPin: VerifyPinUseCase,
        private val settingsRepository: SettingsRepository,
    ) {
        /** FR-100: removes the PIN when [digits] is the current PIN; otherwise returns the failed check. */
        public suspend operator fun invoke(digits: String): PinCheck {
            val check = verifyPin(digits)
            if (check != PinCheck.Accepted) return check
            settingsRepository.update { settings ->
                settings.copy(security = settings.security.copy(pinLock = PinLock(), biometricEnabled = false))
            }
            return check
        }
    }
