package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.meqlause.fintrack.core.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: category writes are suspend, reads are Flow (03 threading). */
@Dao
interface CategoryDao {
    /** DOC-02: inserts a category and returns its generated id. */
    @Insert
    suspend fun insert(category: CategoryEntity): Long

    /** BR-008: fails with a constraint violation while any row still references the category. */
    @Delete
    suspend fun delete(category: CategoryEntity)

    /** DOC-02: every category in manual order, archived ones included. */
    @Query("SELECT * FROM category ORDER BY sortOrder")
    fun observeAll(): Flow<List<CategoryEntity>>
}
