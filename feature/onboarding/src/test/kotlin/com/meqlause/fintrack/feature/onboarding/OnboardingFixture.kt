package com.meqlause.fintrack.feature.onboarding

import com.meqlause.fintrack.core.domain.usecase.CompleteOnboardingUseCase
import com.meqlause.fintrack.core.domain.usecase.SeedDefaultCategoriesUseCase
import com.meqlause.fintrack.core.domain.validation.WalletNameValidator
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.time.FakeClock
import java.time.Instant

// S-01: the fakes behind the onboarding ViewModel, shared by the ViewModel test classes
internal class OnboardingFixture {
    val wallets = FakeWalletRepository()
    val categories = FakeCategoryRepository()
    val settings = FakeSettingsRepository()
    private val clock = FakeClock(Instant.parse("2026-11-22T03:00:00Z"))

    fun viewModel(): OnboardingViewModel {
        val completeOnboarding =
            CompleteOnboardingUseCase(
                walletRepository = wallets,
                settingsRepository = settings,
                seedDefaultCategories = SeedDefaultCategoriesUseCase(categories, { it.name }, clock),
                validator = WalletNameValidator(),
                clock = clock,
            )
        return OnboardingViewModel(completeOnboarding, WalletNameValidator())
    }
}
