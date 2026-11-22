package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.ValuationRepository
import com.meqlause.fintrack.core.model.ValuationEntry
import com.meqlause.fintrack.core.model.ValuationEntryId
import kotlinx.coroutines.flow.Flow

/** ADR-007, FR-172: in-memory valuations ordered by the time they were valued. */
public class FakeValuationRepository : ValuationRepository {
    private val store =
        RowStore<ValuationEntry>(
            idOf = { it.valuationId.value },
            withId = { valuation, newId -> valuation.copy(valuationId = ValuationEntryId(newId)) },
            order = compareBy { it.valuedAt },
        )

    override fun observeAll(): Flow<List<ValuationEntry>> = store.observe()

    override suspend fun insert(valuation: ValuationEntry): ValuationEntryId = ValuationEntryId(store.append(valuation))
}
