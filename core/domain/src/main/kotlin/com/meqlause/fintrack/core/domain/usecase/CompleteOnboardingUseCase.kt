package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.onboarding.CompleteOnboardingResult
import com.meqlause.fintrack.core.domain.onboarding.FirstWalletDraft
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.domain.validation.WalletNameValidator
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

/**
 * FR-002, FR-004: finishes onboarding. Stores the first wallet, seeds the default categories and sets
 * onboardingCompleted (FR-001). Nothing is stored when the wallet name is rejected.
 */
public class CompleteOnboardingUseCase
    @Inject
    constructor(
        private val walletRepository: WalletRepository,
        private val settingsRepository: SettingsRepository,
        private val seedDefaultCategories: SeedDefaultCategoriesUseCase,
        private val validator: WalletNameValidator,
        private val clock: Clock,
    ) {
        /** FR-002: validates the draft, then stores the wallet, the categories and the completed flag. */
        public suspend operator fun invoke(draft: FirstWalletDraft): CompleteOnboardingResult {
            validator.validate(draft.name)?.let { return CompleteOnboardingResult.NameRejected(it) }
            seedDefaultCategories()
            walletRepository.insert(draft.toWallet(clock.instant()))
            settingsRepository.update { settings ->
                settings.copy(preferences = settings.preferences.copy(onboardingCompleted = true))
            }
            return CompleteOnboardingResult.Completed
        }

        // FR-002: the first wallet is the sort-order zero wallet, named as entered without surrounding spaces
        private fun FirstWalletDraft.toWallet(instant: Instant): Wallet {
            val appearance = type.defaultAppearance()
            return Wallet(
                walletId = WalletId(0L),
                kind = WalletKind(type = type, investKind = null),
                terms = WalletTerms(initialBalance = initialBalance, credit = null),
                profile = WalletProfile(name = name.trim(), appearance = appearance, sortOrder = 0),
                status = WalletStatus(),
                audit = Audit(createdAt = instant, updatedAt = instant),
            )
        }

        // FR-002: icon and colour of the onboarding wallet types, copied from the prototype; other types get a neutral default
        private fun WalletType.defaultAppearance(): Appearance =
            when (this) {
                WalletType.CASH -> {
                    Appearance(iconKey = "cash", colorKey = "green")
                }

                WalletType.BANK -> {
                    Appearance(iconKey = "bank", colorKey = "blue")
                }

                WalletType.EWALLET -> {
                    Appearance(iconKey = "ewallet", colorKey = "teal")
                }

                WalletType.CREDIT_CARD, WalletType.PAYLATER, WalletType.INVESTMENT, WalletType.OTHER -> {
                    Appearance(iconKey = "wallet", colorKey = "gray")
                }
            }
    }
