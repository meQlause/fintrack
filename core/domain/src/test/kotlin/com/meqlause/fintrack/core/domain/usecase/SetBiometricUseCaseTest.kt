package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.security.InMemoryPinSettings
import com.meqlause.fintrack.core.model.PinLock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// FR-101: biometric unlock is stored only while a PIN exists
class SetBiometricUseCaseTest {
    private lateinit var settings: InMemoryPinSettings
    private lateinit var setBiometric: SetBiometricUseCase

    @BeforeEach
    fun setUp() {
        settings = InMemoryPinSettings()
        setBiometric = SetBiometricUseCase(settings)
    }

    @Test
    fun `enabling without a PIN is ignored`() {
        runBlocking {
            setBiometric(true)
            assertThat(
                settings
                    .observe()
                    .first()
                    .security.biometricEnabled,
            ).isFalse()
        }
    }

    @Test
    fun `enabling with a PIN is stored`() {
        runBlocking {
            givePin()
            setBiometric(true)
            assertThat(
                settings
                    .observe()
                    .first()
                    .security.biometricEnabled,
            ).isTrue()
        }
    }

    @Test
    fun `disabling turns biometrics off again`() {
        runBlocking {
            givePin()
            setBiometric(true)
            setBiometric(false)
            assertThat(
                settings
                    .observe()
                    .first()
                    .security.biometricEnabled,
            ).isFalse()
        }
    }

    private suspend fun givePin() {
        settings.update { stored ->
            stored.copy(security = stored.security.copy(pinLock = PinLock(pinHash = "hash", pinSalt = "salt")))
        }
    }
}
