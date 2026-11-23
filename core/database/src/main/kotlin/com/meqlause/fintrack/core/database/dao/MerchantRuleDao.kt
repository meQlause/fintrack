package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.meqlause.fintrack.core.database.entity.MerchantRuleEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: merchant rule writes are suspend, reads are Flow (03 threading). */
@Dao
interface MerchantRuleDao {
    /** FR-088: stores a merchant rule and returns its generated id; merchantKey is unique. */
    @Insert
    suspend fun insert(rule: MerchantRuleEntity): Long

    /** FR-088: the merchant rule for one normalized key, or null when the merchant is unknown. */
    @Query("SELECT * FROM merchant_rule WHERE merchantKey = :merchantKey LIMIT 1")
    suspend fun findByKey(merchantKey: String): MerchantRuleEntity?

    /** DOC-02: every merchant rule, by normalized key. */
    @Query("SELECT * FROM merchant_rule ORDER BY merchantKey")
    fun observeAll(): Flow<List<MerchantRuleEntity>>

    /** BR-012, FR-088: replaces the merchant rule with the same id when the merchant is confirmed again. */
    @Update
    suspend fun update(rule: MerchantRuleEntity)

    /** DOC-11: removes every row before a restore. */
    @Query("DELETE FROM merchant_rule")
    suspend fun deleteAll()
}
