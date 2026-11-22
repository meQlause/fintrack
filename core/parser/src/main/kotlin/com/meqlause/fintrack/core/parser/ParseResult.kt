package com.meqlause.fintrack.core.parser

/** FR-083, FR-085, DOC-07: the outcome of parsing one notification. */
public sealed interface ParseResult {
    /** FR-083: the notification describes a transaction. */
    public data class Transaction(
        public val parsed: ParsedTransaction,
    ) : ParseResult

    /** FR-085: the notification is not a transaction and is discarded without storing its text. */
    public data class Ignored(
        public val reason: IgnoreReason,
    ) : ParseResult

    /** FR-085: no template matched; only a counter is incremented. */
    public data object Unrecognized : ParseResult
}
