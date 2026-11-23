package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** FR-101: whether the lock screen offers biometric unlock, emitted again after every change */
public class ObserveBiometricEnabledUseCase
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) {
        /** FR-101: true when the user turned biometric unlock on */
        public operator fun invoke(): Flow<Boolean> = settingsRepository.observe().map { it.security.biometricEnabled }
    }
