package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.ui.inbox.InboxSourceLabel

private const val BADGE_LIMIT = 3

// FR-084, S-03: the detected notices still waiting for review, with the source apps shown as overlapping badges
internal data class HomeInbox(
    val count: Int = 0,
    val sources: List<String> = emptyList(),
) {
    companion object {
        fun fromItems(items: List<InboxItem>): HomeInbox {
            val pending = items.filter { it.resolution.status == InboxStatus.PENDING }
            val sources =
                pending
                    .map {
                        InboxSourceLabel.forPackage(
                            it.notice.packageName,
                        )
                    }.distinct()
                    .take(BADGE_LIMIT)
            return HomeInbox(count = pending.size, sources = sources)
        }
    }
}
