package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.mapper.toEntity
import com.meqlause.fintrack.core.data.mapper.toInboxItem
import com.meqlause.fintrack.core.database.dao.InboxItemDao
import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.Resolution
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

/** DOC-03, ADR-007, FR-090: detected notifications in Room; the fingerprint is unique (FR-092). */
public class LocalInboxRepository(
    private val inboxItemDao: InboxItemDao,
) : InboxRepository {
    override fun observeAll(): Flow<List<InboxItem>> =
        inboxItemDao.observeAll().map { rows -> rows.map { it.toInboxItem() } }

    override suspend fun insert(item: InboxItem): InboxItemId = InboxItemId(inboxItemDao.insert(item.toEntity()))

    override suspend fun containsFingerprint(fingerprint: String): Boolean = inboxItemDao.existsFingerprint(fingerprint)

    override suspend fun pendingItems(): List<InboxItem> = inboxItemDao.pendingItems().map { it.toInboxItem() }

    override suspend fun linkPair(
        first: InboxItemId,
        second: InboxItemId,
    ) {
        inboxItemDao.linkPair(first.value, second.value)
    }

    override suspend fun findById(inboxItemId: InboxItemId): InboxItem? =
        inboxItemDao.findById(inboxItemId.value)?.toInboxItem()

    override suspend fun resolve(
        inboxItemId: InboxItemId,
        resolution: Resolution,
    ) {
        val resolvedAt = resolution.resolvedAt?.toEpochMilli()
        val updatedAt = resolvedAt ?: System.currentTimeMillis()
        inboxItemDao.resolve(
            inboxItemId.value,
            resolution.status,
            resolution.transactionId?.value,
            resolvedAt,
            updatedAt,
        )
    }

    override suspend fun purgeRawTextResolvedBefore(cutoff: Instant) {
        inboxItemDao.purgeRawTextResolvedBefore(cutoff.toEpochMilli())
    }
}
