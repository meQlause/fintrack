package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.Resolution
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant

// FR-090, FR-092: in-memory inbox items for the domain tests; the fingerprint is the dedup key
internal class InMemoryInboxRepository : InboxRepository {
    private val rows = MutableStateFlow<List<InboxItem>>(emptyList())
    private var lastId = 0L

    override fun observeAll(): Flow<List<InboxItem>> = rows

    override suspend fun insert(item: InboxItem): InboxItemId {
        require(rows.value.none { it.notice.fingerprint == item.notice.fingerprint }) { "unique constraint violated" }
        lastId += 1
        val newId = InboxItemId(lastId)
        rows.value = rows.value + item.copy(inboxItemId = newId)
        return newId
    }

    override suspend fun containsFingerprint(fingerprint: String): Boolean =
        rows.value.any { it.notice.fingerprint == fingerprint }

    override suspend fun pendingItems(): List<InboxItem> =
        rows.value.filter { it.resolution.status == InboxStatus.PENDING }

    override suspend fun linkPair(
        first: InboxItemId,
        second: InboxItemId,
    ) {
        rows.value =
            rows.value.map { item ->
                when (item.inboxItemId) {
                    first -> item.withRelated(second)
                    second -> item.withRelated(first)
                    else -> item
                }
            }
    }

    override suspend fun findById(inboxItemId: InboxItemId): InboxItem? =
        rows.value.firstOrNull { it.inboxItemId == inboxItemId }

    override suspend fun resolve(
        inboxItemId: InboxItemId,
        resolution: Resolution,
    ) {
        rows.value =
            rows.value.map { if (it.inboxItemId == inboxItemId) it.copy(resolution = resolution) else it }
    }

    private fun InboxItem.withRelated(partner: InboxItemId): InboxItem =
        copy(suggestion = suggestion.copy(relatedInboxItemId = partner))

    override suspend fun purgeRawTextResolvedBefore(cutoff: Instant) {
        rows.value =
            rows.value.map { item ->
                if (isPurgeable(item, cutoff)) {
                    item.copy(notice = item.notice.copy(rawTitle = "", rawText = ""))
                } else {
                    item
                }
            }
    }

    private fun isPurgeable(
        item: InboxItem,
        cutoff: Instant,
    ): Boolean {
        val resolvedAt = item.resolution.resolvedAt ?: return false
        val resolved = item.resolution.status in setOf(InboxStatus.CONFIRMED, InboxStatus.DISMISSED)
        return resolved && resolvedAt.isBefore(cutoff)
    }
}
