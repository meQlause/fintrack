package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.ValuationRepository
import com.meqlause.fintrack.core.model.ValuationEntry
import com.meqlause.fintrack.core.model.ValuationEntryId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** FR-172: in-memory manual valuations for the investment use case tests. */
internal class InMemoryValuationRepository : ValuationRepository {
    private val rows = MutableStateFlow<List<ValuationEntry>>(emptyList())
    private var lastId = 0L

    override fun observeAll(): Flow<List<ValuationEntry>> = rows

    override suspend fun insert(valuation: ValuationEntry): ValuationEntryId {
        lastId += 1
        val newId = ValuationEntryId(lastId)
        rows.value = rows.value + valuation.copy(valuationId = newId)
        return newId
    }

    override suspend fun delete(valuationId: ValuationEntryId) {
        rows.value = rows.value.filterNot { it.valuationId == valuationId }
    }
}
