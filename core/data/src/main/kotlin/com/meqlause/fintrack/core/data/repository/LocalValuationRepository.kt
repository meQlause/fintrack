package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.mapper.toEntity
import com.meqlause.fintrack.core.data.mapper.toValuationEntry
import com.meqlause.fintrack.core.database.dao.ValuationEntryDao
import com.meqlause.fintrack.core.domain.repository.ValuationRepository
import com.meqlause.fintrack.core.model.ValuationEntry
import com.meqlause.fintrack.core.model.ValuationEntryId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** DOC-03, ADR-007, FR-172: manual valuations of investment wallets in Room (ADR-017). */
public class LocalValuationRepository(
    private val valuationEntryDao: ValuationEntryDao,
) : ValuationRepository {
    override fun observeAll(): Flow<List<ValuationEntry>> =
        valuationEntryDao.observeAll().map { rows -> rows.map { it.toValuationEntry() } }

    override suspend fun insert(valuation: ValuationEntry): ValuationEntryId =
        ValuationEntryId(valuationEntryDao.insert(valuation.toEntity()))

    override suspend fun delete(valuationId: ValuationEntryId) {
        valuationEntryDao.delete(valuationId.value)
    }
}
