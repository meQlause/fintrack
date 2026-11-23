package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.ValuationEntry
import com.meqlause.fintrack.core.model.ValuationEntryId
import kotlinx.coroutines.flow.Flow

/** DOC-03, FR-172: access to manual valuations of investment wallets (ADR-017, BR-051). */
public interface ValuationRepository {
    /** DOC-02: every valuation in the order it was valued. */
    public fun observeAll(): Flow<List<ValuationEntry>>

    /** FR-172: records a valuation and returns its generated id; the valuationId of [valuation] is ignored. */
    public suspend fun insert(valuation: ValuationEntry): ValuationEntryId

    /** BR-054: removes one valuation so that a valuation of the same day can replace it; an unknown id is ignored. */
    public suspend fun delete(valuationId: ValuationEntryId)
}
