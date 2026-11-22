package com.meqlause.fintrack.core.common.time

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

/** DOC-03: verifies that the injectable clock returns the instant it was built with. */
class ClockProviderTest {
    /** DOC-03: a fixed clock yields exactly the fixed instant. */
    @Test
    fun `given fixed clock when instant is read then returns the fixed instant (DOC-03)`() {
        val fixedInstant = Instant.parse("2026-11-22T03:00:00Z")
        val provider =
            object : ClockProvider {
                override fun clock(): Clock = Clock.fixed(fixedInstant, ZoneOffset.UTC)
            }

        val instant = provider.clock().instant()

        assertThat(instant).isEqualTo(fixedInstant)
    }
}
