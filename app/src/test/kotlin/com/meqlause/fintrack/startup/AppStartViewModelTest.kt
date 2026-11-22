package com.meqlause.fintrack.startup

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.usecase.ShouldShowOnboardingUseCase
import com.meqlause.fintrack.core.model.Preferences
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class AppStartViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // FR-001 AC: a fresh install starts on onboarding
    @Test
    fun `given fresh install when started then onboarding is the start destination (FR-001)`() =
        runTest {
            val viewModel =
                AppStartViewModel(ShouldShowOnboardingUseCase(FakeSettingsRepository(), FakeWalletRepository()))

            assertThat(viewModel.showOnboarding.first { it != null }).isEqualTo(true)
        }

    // FR-001 AC: after onboarding the Dashboard is the start destination
    @Test
    fun `given onboarding completed when started then Beranda is the start destination (FR-001)`() =
        runTest {
            val settings = FakeSettingsRepository()
            settings.update { it.copy(preferences = Preferences(onboardingCompleted = true)) }
            val viewModel = AppStartViewModel(ShouldShowOnboardingUseCase(settings, FakeWalletRepository()))

            assertThat(viewModel.showOnboarding.first { it != null }).isEqualTo(false)
        }

    // FR-001: the start destination is decided once, so finishing onboarding cannot rebuild the NavHost
    @Test
    fun `given onboarding needed when the flag changes later then the start destination stays (FR-001)`() =
        runTest {
            val settings = FakeSettingsRepository()
            val viewModel = AppStartViewModel(ShouldShowOnboardingUseCase(settings, FakeWalletRepository()))
            assertThat(viewModel.showOnboarding.first { it != null }).isEqualTo(true)

            settings.update { it.copy(preferences = Preferences(onboardingCompleted = true)) }

            assertThat(viewModel.showOnboarding.value).isEqualTo(true)
        }
}
