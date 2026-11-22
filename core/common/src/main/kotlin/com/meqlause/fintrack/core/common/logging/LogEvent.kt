package com.meqlause.fintrack.core.common.logging

/**
 * DOC-10: a fixed, non-sensitive event name such as `parser.match`. Only lowercase letters, underscores and
 * dots are accepted, so digits (amounts), spaces (free text) and capitals (names) cannot be logged by accident.
 * Event names are constants written in code, never runtime values.
 */
@JvmInline
public value class LogEvent(
    public val name: String,
) {
    init {
        require(name.length in 1..MAX_LENGTH && NAME_PATTERN.matches(name)) { "DOC-10: invalid log event name" }
    }

    private companion object {
        const val MAX_LENGTH = 64
        val NAME_PATTERN = Regex("[a-z_]+(\\.[a-z_]+)*")
    }
}
