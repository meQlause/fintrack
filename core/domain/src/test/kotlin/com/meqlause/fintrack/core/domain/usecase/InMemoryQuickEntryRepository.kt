package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.QuickEntryRepository
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

// FR-039: an in-memory shortcut store for use-case tests; ids are assigned in insertion order

/** In memory quick entry repository tests. */
class InMemoryQuickEntryRepository : QuickEntryRepository {
    private val rows = MutableStateFlow<List<QuickEntry>>(emptyList())

    override fun observeAll(): Flow<List<QuickEntry>> = rows.map { list -> list.sortedBy { it.sortOrder } }

    override suspend fun insert(quickEntry: QuickEntry): QuickEntryId {
        val newId = QuickEntryId(rows.value.size + 1L)
        rows.value = rows.value + quickEntry.copy(quickEntryId = newId)
        return newId
    }

    override suspend fun update(quickEntry: QuickEntry) {
        rows.value = rows.value.map { if (it.quickEntryId == quickEntry.quickEntryId) quickEntry else it }
    }

    override suspend fun delete(quickEntryId: QuickEntryId) {
        rows.value = rows.value.filterNot { it.quickEntryId == quickEntryId }
    }
}
