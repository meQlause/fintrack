package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.meqlause.fintrack.core.database.entity.ValuationEntryEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: valuation writes are suspend, reads are Flow (03 threading). */
@Dao
interface ValuationEntryDao {
    /** FR-172: records a manual valuation and returns its generated id. */
    @Insert
    suspend fun insert(valuation: ValuationEntryEntity): Long

    /** BR-054: removes one valuation by its id. */
    @Query("DELETE FROM valuation_entry WHERE valuationId = :valuationId")
    suspend fun delete(valuationId: Long)

    /** DOC-02: every valuation in the order it was valued. */
    @Query("SELECT * FROM valuation_entry ORDER BY valuedAt")
    fun observeAll(): Flow<List<ValuationEntryEntity>>

    /** DOC-11: removes every row before a restore. */
    @Query("DELETE FROM valuation_entry")
    suspend fun deleteAll()
}
