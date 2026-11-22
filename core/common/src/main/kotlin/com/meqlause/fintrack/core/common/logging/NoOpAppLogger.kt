package com.meqlause.fintrack.core.common.logging

/** DOC-10: release implementation of [AppLogger]. Discards every event so nothing reaches logcat. */
public class NoOpAppLogger : AppLogger {
    /** DOC-10: discards the event; nothing is written anywhere. */
    override fun record(
        level: LogLevel,
        event: LogEvent,
    ) {
        // DOC-10: intentionally empty, release builds must not log
    }
}
