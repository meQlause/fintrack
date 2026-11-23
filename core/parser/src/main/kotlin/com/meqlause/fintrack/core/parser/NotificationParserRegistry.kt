package com.meqlause.fintrack.core.parser

/** FR-083, DOC-07: routes a notification to the parsers registered for its package, in registration order. */
public class NotificationParserRegistry(
    private val parsers: List<NotificationParser>,
) {
    /** FR-085: the first parser that recognises the notification decides; none registered means Unrecognized. */
    public fun parse(raw: RawNotification): ParseResult =
        parsers
            .asSequence()
            .filter { raw.packageName in it.packageNames }
            .map { it.parse(raw) }
            .firstOrNull { it !is ParseResult.Unrecognized }
            ?: ParseResult.Unrecognized
}
