package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.meqlause.fintrack.core.database.entity.InboxItemEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: inbox writes are suspend, reads are Flow (03 threading). */
@Dao
interface InboxItemDao {
    /** FR-090: stores a detected notification and returns its generated id; a duplicate fingerprint is rejected. */
    @Insert
    suspend fun insert(item: InboxItemEntity): Long

    /** DOC-02: every inbox item, oldest notification first. */
    @Query("SELECT * FROM inbox_item ORDER BY postedAt")
    fun observeAll(): Flow<List<InboxItemEntity>>

    /** FR-092: true when an inbox item with this dedup fingerprint already exists. */
    @Query("SELECT EXISTS(SELECT 1 FROM inbox_item WHERE fingerprint = :fingerprint)")
    suspend fun existsFingerprint(fingerprint: String): Boolean

    /** FR-091: every item still waiting for review, oldest notification first. */
    @Query("SELECT * FROM inbox_item WHERE status = 'PENDING' ORDER BY postedAt")
    suspend fun pendingItems(): List<InboxItemEntity>

    /** FR-091: points both items at each other in one statement. */
    @Query(
        "UPDATE inbox_item SET relatedInboxItemId = " +
            "CASE WHEN inboxItemId = :first THEN :second ELSE :first END " +
            "WHERE inboxItemId IN (:first, :second)",
    )
    suspend fun linkPair(
        first: Long,
        second: Long,
    )

    /** FR-087: the inbox item with this id, or null when it does not exist. */
    @Query("SELECT * FROM inbox_item WHERE inboxItemId = :inboxItemId LIMIT 1")
    suspend fun findById(inboxItemId: Long): InboxItemEntity?

    /** BR-011, FR-087: stores a reviewed row with its new status, transaction and resolution time. */
    @Update
    suspend fun update(item: InboxItemEntity)

    /** FR-092: clears raw title and text of CONFIRMED or DISMISSED items resolved before the cutoff (epoch millis). */
    @Query(
        "UPDATE inbox_item SET rawTitle = '', rawText = '' " +
            "WHERE status IN ('CONFIRMED', 'DISMISSED') AND resolvedAt IS NOT NULL AND resolvedAt < :resolvedBefore",
    )
    suspend fun purgeRawTextResolvedBefore(resolvedBefore: Long)

    /** DOC-11: removes every row before a restore. */
    @Query("DELETE FROM inbox_item")
    suspend fun deleteAll()
}
