package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import kotlinx.coroutines.flow.Flow

/** DOC-03, FR-090: access to notifications detected as payments and waiting for review (FR-091). */
public interface InboxRepository {
    /** DOC-02: every inbox item, oldest notification first. */
    public fun observeAll(): Flow<List<InboxItem>>

    /** FR-090: stores a detected item and returns its generated id; an item with a known fingerprint is rejected. */
    public suspend fun insert(item: InboxItem): InboxItemId
}
