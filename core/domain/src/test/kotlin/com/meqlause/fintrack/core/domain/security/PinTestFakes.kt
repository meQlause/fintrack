package com.meqlause.fintrack.core.domain.security

import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

// FR-100: in-memory settings for PIN tests; starts at the DOC-02 defaults
internal class InMemoryPinSettings : SettingsRepository {
    private val settings = MutableStateFlow(Settings())

    override fun observe(): Flow<Settings> = settings

    override suspend fun update(transform: (Settings) -> Settings) {
        settings.update(transform)
    }
}

// DOC-10: a hasher that is easy to read in assertions; the real PBKDF2 lives in core:security
internal class ReadablePinHasher : PinHasher {
    private var counter = 0

    override fun newSalt(): String {
        counter++
        return "salt$counter"
    }

    override fun hash(
        digits: String,
        salt: String,
    ): String = "hash($digits,$salt)"

    override fun matches(
        digits: String,
        salt: String,
        expectedHash: String,
    ): Boolean = hash(digits, salt) == expectedHash
}

// FR-103: a clock that only moves when a test moves it
internal class MovableClock(
    var instant: Instant,
) : Clock() {
    override fun getZone(): ZoneId = ZoneId.of("UTC")

    override fun withZone(zone: ZoneId): Clock = this

    override fun instant(): Instant = instant

    fun advanceSeconds(seconds: Long) {
        instant = instant.plusSeconds(seconds)
    }
}
