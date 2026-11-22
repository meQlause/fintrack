package com.meqlause.fintrack.core.common.time

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import java.time.ZoneId

/** DOC-03: verifies that the production clock uses the device default time zone. */
class SystemClockProviderTest {
    /** DOC-03: the zone is deterministic even though the instant is not. */
    @Test
    fun `given system provider when clock is read then uses the system default zone (DOC-03)`() {
        val clock = SystemClockProvider().clock()

        assertThat(clock.zone).isEqualTo(ZoneId.systemDefault())
    }
}
