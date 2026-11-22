package com.meqlause.fintrack.core.testing.time

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class FakeClockZoneTest {
    @Test
    fun `given fake clock with zone when zone is changed then the instant stays the same (DOC-06)`() {
        val clock = FakeClock(Instant.parse("2026-11-22T03:00:00Z"), ZoneId.of("UTC"))

        val shifted = clock.withZone(ZoneId.of("Asia/Jakarta"))

        assertThat(clock.zone).isEqualTo(ZoneId.of("UTC"))
        assertThat(shifted.zone).isEqualTo(ZoneId.of("Asia/Jakarta"))
        assertThat(shifted.instant()).isEqualTo(Instant.parse("2026-11-22T03:00:00Z"))
    }
}
