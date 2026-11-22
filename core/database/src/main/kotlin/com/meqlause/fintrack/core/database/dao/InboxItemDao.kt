package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
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
}
