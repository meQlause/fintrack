package com.meqlause.fintrack.di

import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

// DOC-06: UI tests run without the Android Keystore; the repositories are in-memory fakes with onboarding already done
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])
object TestRepositoryModule {
    @Provides
    @Singleton
    fun provideWalletRepository(): WalletRepository = FakeWalletRepository()

    @Provides
    @Singleton
    fun provideCategoryRepository(): CategoryRepository = FakeCategoryRepository()

    @Provides
    @Singleton
    fun provideSettingsRepository(): SettingsRepository {
        val settings = FakeSettingsRepository()
        runBlocking {
            settings.update { current ->
                current.copy(preferences = current.preferences.copy(onboardingCompleted = true))
            }
        }
        return settings
    }
}
