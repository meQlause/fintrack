package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.MerchantRule
import com.meqlause.fintrack.core.model.MerchantRuleId
import kotlinx.coroutines.flow.Flow

/** DOC-03, FR-088: access to merchant memory; one rule per normalized merchant key (BR-012). */
public interface MerchantRuleRepository {
    /** DOC-02: every merchant rule, by normalized key. */
    public fun observeAll(): Flow<List<MerchantRule>>

    /** FR-088: stores a rule and returns its generated id; a second rule for the same merchant key is rejected. */
    public suspend fun insert(rule: MerchantRule): MerchantRuleId
}
