package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.mapper.toEntity
import com.meqlause.fintrack.core.data.mapper.toQuickEntry
import com.meqlause.fintrack.core.database.dao.QuickEntryDao
import com.meqlause.fintrack.core.domain.repository.QuickEntryRepository
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** DOC-03, ADR-007, FR-039: shortcuts in Room; applying one only pre-fills a draft (BR-047). */
public class LocalQuickEntryRepository(
    private val quickEntryDao: QuickEntryDao,
) : QuickEntryRepository {
    override fun observeAll(): Flow<List<QuickEntry>> =
        quickEntryDao.observeAll().map { rows -> rows.map { it.toQuickEntry() } }

    override suspend fun insert(quickEntry: QuickEntry): QuickEntryId =
        QuickEntryId(quickEntryDao.insert(quickEntry.toEntity()))
}
