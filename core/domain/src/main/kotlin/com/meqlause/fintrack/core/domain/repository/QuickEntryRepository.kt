package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import kotlinx.coroutines.flow.Flow

/** DOC-03, FR-039: access to shortcuts that pre-fill the add sheet (BR-047). */
public interface QuickEntryRepository {
    /** DOC-02: every shortcut in the user's chosen order. */
    public fun observeAll(): Flow<List<QuickEntry>>

    /** FR-039: stores a shortcut and returns its generated id; the quickEntryId of [quickEntry] is ignored. */
    public suspend fun insert(quickEntry: QuickEntry): QuickEntryId

    /** FR-039: replaces the stored shortcut that has the same quickEntryId. */
    public suspend fun update(quickEntry: QuickEntry)

    /** FR-039: removes a shortcut from the manager; the transactions it once filled are untouched. */
    public suspend fun delete(quickEntryId: QuickEntryId)
}
