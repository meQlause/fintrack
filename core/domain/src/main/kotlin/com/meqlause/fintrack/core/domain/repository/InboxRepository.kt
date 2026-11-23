package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.Resolution
import kotlinx.coroutines.flow.Flow

/** DOC-03, FR-090: access to notifications detected as payments and waiting for review (FR-091). */
public interface InboxRepository {
    /** DOC-02: every inbox item, oldest notification first. */
    public fun observeAll(): Flow<List<InboxItem>>

    /** FR-090: stores a detected item and returns its generated id; an item with a known fingerprint is rejected. */
    public suspend fun insert(item: InboxItem): InboxItemId

    /** FR-092: true when an inbox item with this dedup fingerprint already exists. */
    public suspend fun containsFingerprint(fingerprint: String): Boolean

    /** FR-091: every item still waiting for review, oldest notification first. */
    public suspend fun pendingItems(): List<InboxItem>

    /** FR-091: makes two items each other's pair partner. */
    public suspend fun linkPair(
        first: InboxItemId,
        second: InboxItemId,
    )

    /** FR-087: the inbox item with this id, or null when it does not exist. */
    public suspend fun findById(inboxItemId: InboxItemId): InboxItem?

    /** BR-011, FR-087: stores the review status, the created transaction and the resolution time of an item. */
    public suspend fun resolve(
        inboxItemId: InboxItemId,
        resolution: Resolution,
    )
}
