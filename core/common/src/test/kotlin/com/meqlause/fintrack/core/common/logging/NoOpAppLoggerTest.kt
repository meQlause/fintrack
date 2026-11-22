package com.meqlause.fintrack.core.common.logging

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

/** DOC-10: verifies the release logger discards every event without failing. */
class NoOpAppLoggerTest {
    /** DOC-10: every level is accepted and nothing is thrown. */
    @Test
    fun `given no-op logger when every level is recorded then nothing is thrown (DOC-10)`() {
        val logger = NoOpAppLogger()

        assertDoesNotThrow {
            LogLevel.entries.forEach { level ->
                logger.record(level, LogEvent("parser.match"))
            }
        }
    }
}
