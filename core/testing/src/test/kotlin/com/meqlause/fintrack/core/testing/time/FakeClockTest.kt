package com.meqlause.fintrack.core.testing.time

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import java.time.Instant
import kotlin.time.Duration.Companion.hours

class FakeClockTest {
    @Test
    fun `given fake clock when advanced then instant moves (DOC-06)`() {
        val clock = FakeClock(Instant.parse("2026-11-22T03:00:00Z"))

        clock.advanceBy(2.hours)

        assertThat(clock.instant()).isEqualTo(Instant.parse("2026-11-22T05:00:00Z"))
    }
}
