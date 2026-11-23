package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.model.PinLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** FR-100, FR-103: the stored PIN state, emitted again after every change. */
public class ObservePinLockUseCase
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) {
        /** FR-100: emits the PIN hash, salt and lockout state. */
        public operator fun invoke(): Flow<PinLock> = settingsRepository.observe().map { it.security.pinLock }
    }
