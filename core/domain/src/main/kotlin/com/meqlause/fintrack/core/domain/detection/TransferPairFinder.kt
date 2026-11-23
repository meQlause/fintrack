package com.meqlause.fintrack.core.domain.detection

import com.meqlause.fintrack.core.model.InboxItem
import java.time.Duration

/** FR-091, BR-013: finds the pending item that a new detected payment forms a transfer pair with. */
public object TransferPairFinder {
    private val PAIR_WINDOW: Duration = Duration.ofMinutes(10)

    /** FR-091: the oldest unpaired pending item of another app, opposite direction, same amount, within 10 min. */
    public fun findPartner(
        item: InboxItem,
        pending: List<InboxItem>,
    ): InboxItem? = pending.filter { isPartner(item, it) }.minByOrNull { it.notice.postedAt }

    private fun isPartner(
        item: InboxItem,
        other: InboxItem,
    ): Boolean =
        other.suggestion.relatedInboxItemId == null &&
            other.notice.packageName != item.notice.packageName &&
            other.parsed.direction != item.parsed.direction &&
            other.parsed.amount == item.parsed.amount &&
            Duration.between(item.notice.postedAt, other.notice.postedAt).abs() <= PAIR_WINDOW
}
