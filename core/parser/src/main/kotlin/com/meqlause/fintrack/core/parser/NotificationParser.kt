package com.meqlause.fintrack.core.parser

import com.meqlause.fintrack.core.model.RawNotification

/** FR-083, DOC-07: one app-specific parser; it must never throw and returns Unrecognized for unexpected input. */
public interface NotificationParser {
    /** DOC-07: stable identifier stored on each inbox item, e.g. "bca.v1". */
    public val parserId: String

    /** DOC-07: package names of the apps this parser reads. */
    public val packageNames: Set<String>

    /** FR-083, DOC-07: the outcome for one notification of a package this parser covers. */
    public fun parse(rawNotification: RawNotification): ParseResult
}
