package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.meqlause.fintrack.core.database.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: wallet writes are suspend, reads are Flow (03 threading). */
@Dao
interface WalletDao {
    /** DOC-02: inserts a wallet and returns its generated id. */
    @Insert
    suspend fun insert(wallet: WalletEntity): Long

    /** BR-008: fails with a constraint violation while any row still references the wallet. */
    @Delete
    suspend fun delete(wallet: WalletEntity)

    /** DOC-02: every wallet in manual order, archived ones included. */
    @Query("SELECT * FROM wallet ORDER BY sortOrder")
    fun observeAll(): Flow<List<WalletEntity>>
}
