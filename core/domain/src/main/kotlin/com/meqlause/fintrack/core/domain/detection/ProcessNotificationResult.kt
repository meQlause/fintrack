package com.meqlause.fintrack.core.domain.detection

import com.meqlause.fintrack.core.model.InboxItemId

/** FR-084, FR-085, FR-086: what happened to one notification; nothing is stored unless it is [Stored]. */
public sealed interface ProcessNotificationResult {
    /** FR-084: stored as PENDING; [pairedWith] is the partner item when a transfer pair was found (FR-091). */
    public data class Stored(
        public val inboxItemId: InboxItemId,
        public val pairedWith: InboxItemId?,
    ) : ProcessNotificationResult

    /** FR-086: the same notification was already stored; it is discarded. */
    public data object Duplicate : ProcessNotificationResult

    /** FR-090: the app is not a monitored, enabled app; nothing is read. */
    public data object NotMonitored : ProcessNotificationResult
}
