package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.Resolution
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.Instant

/** ADR-007, FR-090: in-memory inbox items; the fingerprint is the dedup key (FR-092). */
public class FakeInboxRepository : InboxRepository {
    private val store =
        RowStore<InboxItem>(
            idOf = { it.inboxItemId.value },
            withId = { item, newId -> item.copy(inboxItemId = InboxItemId(newId)) },
            order = compareBy { it.notice.postedAt },
        )

    override fun observeAll(): Flow<List<InboxItem>> = store.observe()

    override suspend fun insert(item: InboxItem): InboxItemId =
        InboxItemId(store.append(item) { existing -> existing.notice.fingerprint == item.notice.fingerprint })

    override suspend fun containsFingerprint(fingerprint: String): Boolean =
        store.snapshot().any { it.notice.fingerprint == fingerprint }

    override suspend fun pendingItems(): List<InboxItem> =
        store.observe().first().filter { it.resolution.status == InboxStatus.PENDING }

    override suspend fun linkPair(
        first: InboxItemId,
        second: InboxItemId,
    ) {
        store.snapshot().forEach { item ->
            val partner = partnerOf(item.inboxItemId, first, second) ?: return@forEach
            store.replace(item.copy(suggestion = item.suggestion.copy(relatedInboxItemId = partner)))
        }
    }

    private fun partnerOf(
        itemId: InboxItemId,
        first: InboxItemId,
        second: InboxItemId,
    ): InboxItemId? =
        when (itemId) {
            first -> second
            second -> first
            else -> null
        }

    override suspend fun findById(inboxItemId: InboxItemId): InboxItem? =
        store.snapshot().firstOrNull { it.inboxItemId == inboxItemId }

    override suspend fun resolve(
        inboxItemId: InboxItemId,
        resolution: Resolution,
    ) {
        store.snapshot().firstOrNull { it.inboxItemId == inboxItemId }?.let {
            store.replace(
                it.copy(resolution = resolution),
            )
        }
    }

    override suspend fun purgeRawTextResolvedBefore(cutoff: Instant) {
        store.snapshot().filter { purgeable(it, cutoff) }.forEach { item ->
            store.replace(item.copy(notice = item.notice.copy(rawTitle = "", rawText = "")))
        }
    }

    // FR-092: only resolved items whose resolution is older than the cutoff lose their raw text
    private fun purgeable(
        item: InboxItem,
        cutoff: Instant,
    ): Boolean {
        val resolvedAt = item.resolution.resolvedAt ?: return false
        val resolved = item.resolution.status in setOf(InboxStatus.CONFIRMED, InboxStatus.DISMISSED)
        return resolved && resolvedAt.isBefore(cutoff)
    }
}
