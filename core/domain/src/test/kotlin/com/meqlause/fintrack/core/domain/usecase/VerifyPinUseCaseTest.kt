package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.domain.security.InMemoryPinSettings
import com.meqlause.fintrack.core.domain.security.MovableClock
import com.meqlause.fintrack.core.domain.security.PinCheck
import com.meqlause.fintrack.core.domain.security.ReadablePinHasher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

// FR-100, FR-103: PIN checks, the failure count and the lockout

/** Verify pin use case tests. */
class VerifyPinUseCaseTest {
    private lateinit var settings: InMemoryPinSettings
    private lateinit var clock: MovableClock
    private lateinit var verifyPin: VerifyPinUseCase

    /** Set up. */
    @BeforeEach
    fun setUp() {
        settings = InMemoryPinSettings()
        clock = MovableClock(Instant.parse("2026-11-23T10:00:00Z"))
        verifyPin = VerifyPinUseCase(settings, ReadablePinHasher(), clock)
        runBlocking { SetPinUseCase(settings, ReadablePinHasher())("123456") }
    }

    /** Correct PIN is accepted and clears failures. */
    @Test
    fun `correct PIN is accepted and clears failures`() {
        runBlocking {
            verifyPin("000000")
            assertThat(verifyPin("123456")).isEqualTo(PinCheck.Accepted)
            assertThat(
                settings
                    .observe()
                    .first()
                    .security.pinLock.failedPinAttempts,
            ).isEqualTo(0)
        }
    }

    /** First four wrong PINs are rejected without a lockout. */
    @Test
    fun `first four wrong PINs are rejected without a lockout`() {
        runBlocking {
            repeat(4) { assertThat(verifyPin("000000")).isEqualTo(PinCheck.Rejected) }
        }
    }

    /** Fifth wrong PIN locks for 30 seconds. */
    @Test
    fun `fifth wrong PIN locks for 30 seconds`() {
        runBlocking {
            repeat(4) { verifyPin("000000") }
            val check = verifyPin("000000")
            assertThat(check).isInstanceOf(PinCheck.LockedOut::class)
            assertThat((check as PinCheck.LockedOut).until).isEqualTo(clock.instant.plusSeconds(30))
        }
    }

    /** Correct PIN is refused during the lockout and not counted. */
    @Test
    fun `correct PIN is refused during the lockout and not counted`() {
        runBlocking {
            repeat(5) { verifyPin("000000") }
            clock.advanceSeconds(10)
            assertThat(verifyPin("123456")).isInstanceOf(PinCheck.LockedOut::class)
            assertThat(
                settings
                    .observe()
                    .first()
                    .security.pinLock.failedPinAttempts,
            ).isEqualTo(5)
        }
    }

    /** PIN works again after the lockout ends. */
    @Test
    fun `PIN works again after the lockout ends`() {
        runBlocking {
            repeat(5) { verifyPin("000000") }
            clock.advanceSeconds(30)
            assertThat(verifyPin("123456")).isEqualTo(PinCheck.Accepted)
        }
    }

    /** A wrong PIN after the wait doubles the lockout to 60 seconds. */
    @Test
    fun `a wrong PIN after the wait doubles the lockout to 60 seconds`() {
        runBlocking {
            repeat(5) { verifyPin("000000") }
            clock.advanceSeconds(30)
            val check = verifyPin("000000")
            assertThat((check as PinCheck.LockedOut).until).isEqualTo(clock.instant.plusSeconds(60))
        }
    }

    /** No PIN set means every check is accepted. */
    @Test
    fun `no PIN set means every check is accepted`() {
        runBlocking {
            val empty = InMemoryPinSettings()
            val check = VerifyPinUseCase(empty, ReadablePinHasher(), clock).invoke("000000")
            assertThat(check).isEqualTo(PinCheck.Accepted)
        }
    }
}
