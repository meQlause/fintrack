package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.mapper.toEntity
import com.meqlause.fintrack.core.data.mapper.toInboxItem
import com.meqlause.fintrack.core.database.dao.InboxItemDao
import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** DOC-03, ADR-007, FR-090: detected notifications in Room; the fingerprint is unique (FR-092). */
public class LocalInboxRepository(
    private val inboxItemDao: InboxItemDao,
) : InboxRepository {
    override fun observeAll(): Flow<List<InboxItem>> =
        inboxItemDao.observeAll().map { rows -> rows.map { it.toInboxItem() } }

    override suspend fun insert(item: InboxItem): InboxItemId = InboxItemId(inboxItemDao.insert(item.toEntity()))
}
