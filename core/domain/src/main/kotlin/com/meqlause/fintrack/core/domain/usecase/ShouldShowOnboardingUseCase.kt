package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/** FR-001: onboarding is shown only while no wallet exists and onboardingCompleted is false. */
public class ShouldShowOnboardingUseCase
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
        private val walletRepository: WalletRepository,
    ) {
        /** FR-001: emits true while onboarding must be shown, again after every change to either source. */
        public operator fun invoke(): Flow<Boolean> =
            combine(settingsRepository.observe(), walletRepository.observeAll()) { settings, wallets ->
                !settings.preferences.onboardingCompleted && wallets.isEmpty()
            }
    }
