package com.meqlause.fintrack.core.domain.quickentry

import com.meqlause.fintrack.core.model.QuickEntryId

/** FR-039: outcome of saving a shortcut; a rejected shortcut stores nothing. */
public sealed interface QuickEntryResult {
    /** FR-039: the shortcut is stored under [quickEntryId]. */
    public data class Saved(
        public val quickEntryId: QuickEntryId,
    ) : QuickEntryResult

    /** FR-039, BR-047: every rule the shortcut breaks. */
    public data class Rejected(
        public val errors: Set<QuickEntryError>,
    ) : QuickEntryResult
}
