package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.meqlause.fintrack.core.database.entity.QuickEntryEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: quick entry writes are suspend, reads are Flow (03 threading). */
@Dao
interface QuickEntryDao {
    /** FR-039: stores a shortcut and returns its generated id. */
    @Insert
    suspend fun insert(entry: QuickEntryEntity): Long

    /** FR-039: replaces the stored shortcut that has the same id. */
    @Update
    suspend fun update(entry: QuickEntryEntity)

    /** DOC-02: every shortcut in the user's chosen order. */
    @Query("SELECT * FROM quick_entry ORDER BY sortOrder")
    fun observeAll(): Flow<List<QuickEntryEntity>>

    /** FR-039: removes a shortcut; the manager offers undo for five seconds. */
    @Query("DELETE FROM quick_entry WHERE quickEntryId = :quickEntryId")
    suspend fun delete(quickEntryId: Long)

    /** DOC-11: removes every row before a restore. */
    @Query("DELETE FROM quick_entry")
    suspend fun deleteAll()
}
