package com.meqlause.fintrack.core.domain.security

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

// FR-100, FR-103: PIN format and lockout schedule
class PinPolicyTest {
    @Test
    fun `accepts exactly six digits`() {
        assertThat(PinPolicy.isValid("123456")).isTrue()
        assertThat(PinPolicy.isValid("000000")).isTrue()
    }

    @Test
    fun `rejects wrong length, letters and signs`() {
        assertThat(PinPolicy.isValid("12345")).isFalse()
        assertThat(PinPolicy.isValid("1234567")).isFalse()
        assertThat(PinPolicy.isValid("12a456")).isFalse()
        assertThat(PinPolicy.isValid("-23456")).isFalse()
        assertThat(PinPolicy.isValid("")).isFalse()
    }

    @Test
    fun `no lockout while the first five attempts are free`() {
        (0..4).forEach { assertThat(PinPolicy.lockSeconds(it)).isEqualTo(0L) }
    }

    @Test
    fun `lockout starts at 30 seconds and doubles with each further failure`() {
        assertThat(PinPolicy.lockSeconds(5)).isEqualTo(30L)
        assertThat(PinPolicy.lockSeconds(6)).isEqualTo(60L)
        assertThat(PinPolicy.lockSeconds(7)).isEqualTo(120L)
        assertThat(PinPolicy.lockSeconds(8)).isEqualTo(240L)
    }

    @Test
    fun `lockout is capped at 15 minutes and huge counts do not overflow`() {
        assertThat(PinPolicy.lockSeconds(10)).isEqualTo(900L)
        assertThat(PinPolicy.lockSeconds(Int.MAX_VALUE)).isEqualTo(900L)
    }
}
