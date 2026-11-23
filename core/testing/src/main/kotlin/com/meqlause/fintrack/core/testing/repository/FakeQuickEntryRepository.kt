package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.QuickEntryRepository
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import kotlinx.coroutines.flow.Flow

/** ADR-007, FR-039: in-memory quick entries in the user's chosen order. */
public class FakeQuickEntryRepository : QuickEntryRepository {
    private val store =
        RowStore<QuickEntry>(
            idOf = { it.quickEntryId.value },
            withId = { entry, newId -> entry.copy(quickEntryId = QuickEntryId(newId)) },
            order = compareBy { it.sortOrder },
        )

    override fun observeAll(): Flow<List<QuickEntry>> = store.observe()

    override suspend fun insert(quickEntry: QuickEntry): QuickEntryId = QuickEntryId(store.append(quickEntry))

    override suspend fun delete(quickEntryId: QuickEntryId) = store.remove(quickEntryId.value)
}
