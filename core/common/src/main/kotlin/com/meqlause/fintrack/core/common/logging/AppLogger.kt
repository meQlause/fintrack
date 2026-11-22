package com.meqlause.fintrack.core.common.logging

/**
 * DOC-10: the only logging entry point. Callers pass a level and a fixed [LogEvent] name, never free text.
 * Amounts, merchants, wallet names, notification text and PIN data must never be logged (DOC-10, 05 §5).
 */
public interface AppLogger {
    /** DOC-10: records [event] at [level]. Release builds discard every event. */
    public fun record(
        level: LogLevel,
        event: LogEvent,
    )
}
