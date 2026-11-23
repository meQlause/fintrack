package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import javax.inject.Inject

// FR-101: turns biometric unlock on or off; biometrics can only be on while a PIN exists
public class SetBiometricUseCase
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) {
        // FR-101: enabling is ignored when no PIN is set, because the PIN is the fallback
        public suspend operator fun invoke(enabled: Boolean) {
            settingsRepository.update { settings ->
                val hasPin = settings.security.pinLock.pinHash != null
                settings.copy(security = settings.security.copy(biometricEnabled = enabled && hasPin))
            }
        }
    }
