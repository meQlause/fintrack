package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.security.InMemoryPinSettings
import com.meqlause.fintrack.core.domain.security.MovableClock
import com.meqlause.fintrack.core.domain.security.PinCheck
import com.meqlause.fintrack.core.domain.security.ReadablePinHasher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

// FR-100, DOC-10: setting, changing and removing the PIN; only salt and hash are stored

/** Pin setup use cases tests. */
class PinSetupUseCasesTest {
    private lateinit var settings: InMemoryPinSettings
    private lateinit var setPin: SetPinUseCase
    private lateinit var removePin: RemovePinUseCase

    /** Set up. */
    @BeforeEach
    fun setUp() {
        settings = InMemoryPinSettings()
        val hasher = ReadablePinHasher()
        val clock = MovableClock(Instant.parse("2026-11-23T10:00:00Z"))
        setPin = SetPinUseCase(settings, hasher)
        removePin = RemovePinUseCase(VerifyPinUseCase(settings, hasher, clock), settings)
    }

    /** Invalid PIN is refused and nothing is stored. */
    @Test
    fun `invalid PIN is refused and nothing is stored`() {
        runBlocking {
            assertThat(setPin("12345")).isFalse()
            assertThat(
                settings
                    .observe()
                    .first()
                    .security.pinLock.pinHash,
            ).isNull()
        }
    }

    /** Valid PIN is stored as hash and salt, never as digits. */
    @Test
    fun `valid PIN is stored as hash and salt, never as digits`() {
        runBlocking {
            assertThat(setPin("123456")).isTrue()
            val pinLock =
                settings
                    .observe()
                    .first()
                    .security.pinLock
            assertThat(pinLock.pinHash).isNotEqualTo("123456")
            assertThat(pinLock.pinSalt).isNotNull()
        }
    }

    /** Removing with the wrong PIN keeps the PIN. */
    @Test
    fun `removing with the wrong PIN keeps the PIN`() {
        runBlocking {
            setPin("123456")
            assertThat(removePin("000000")).isEqualTo(PinCheck.Rejected)
            assertThat(
                settings
                    .observe()
                    .first()
                    .security.pinLock.pinHash,
            ).isNotNull()
        }
    }

    /** Removing with the right PIN clears the PIN and biometrics. */
    @Test
    fun `removing with the right PIN clears the PIN and biometrics`() {
        runBlocking {
            setPin("123456")
            assertThat(removePin("123456")).isEqualTo(PinCheck.Accepted)
            val security = settings.observe().first().security
            assertThat(security.pinLock.pinHash).isNull()
            assertThat(security.biometricEnabled).isFalse()
        }
    }
}
