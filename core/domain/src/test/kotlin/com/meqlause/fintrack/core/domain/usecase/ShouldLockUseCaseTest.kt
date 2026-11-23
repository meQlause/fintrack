package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.model.PinLock
import com.meqlause.fintrack.core.model.Security
import org.junit.jupiter.api.Test
import java.time.Instant

// FR-102: the lock shows after the background timeout, and at every cold start while a PIN exists

/** Should lock use case tests. */
class ShouldLockUseCaseTest {
    private val useCase = ShouldLockUseCase()
    private val back = Instant.parse("2026-11-23T10:00:00Z")
    private val withPin = Security(pinLock = PinLock(pinHash = "hash", pinSalt = "salt"), lockTimeoutSeconds = 30)

    /** No lock without a PIN. */
    @Test
    fun `no lock without a PIN`() {
        assertThat(useCase(Security(lockTimeoutSeconds = 0), back, back.plusSeconds(999))).isFalse()
    }

    /** No lock before the timeout has passed. */
    @Test
    fun `no lock before the timeout has passed`() {
        assertThat(useCase(withPin, back, back.plusSeconds(29))).isFalse()
    }

    /** Lock once the timeout has passed. */
    @Test
    fun `lock once the timeout has passed`() {
        assertThat(useCase(withPin, back, back.plusSeconds(30))).isTrue()
    }

    /** Cold start with a PIN locks because nothing was backgrounded recently. */
    @Test
    fun `cold start with a PIN locks because nothing was backgrounded recently`() {
        assertThat(useCase(withPin, Instant.EPOCH, back)).isTrue()
    }
}
