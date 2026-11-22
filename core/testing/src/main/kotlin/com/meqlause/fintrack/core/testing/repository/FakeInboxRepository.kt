package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import kotlinx.coroutines.flow.Flow

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
}
