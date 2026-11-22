package com.meqlause.fintrack.core.testing.time

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration

// DOC-06: settable clock; time only moves when a test advances it, so tests are deterministic
class FakeClock(
    private var currentInstant: Instant,
    private val zoneId: ZoneId = ZoneId.of("Asia/Jakarta"),
) : Clock() {
    override fun getZone(): ZoneId = zoneId

    override fun withZone(zone: ZoneId): Clock = FakeClock(currentInstant, zone)

    override fun instant(): Instant = currentInstant

    fun advanceBy(duration: Duration) {
        currentInstant = currentInstant.plusMillis(duration.inWholeMilliseconds)
    }
}
