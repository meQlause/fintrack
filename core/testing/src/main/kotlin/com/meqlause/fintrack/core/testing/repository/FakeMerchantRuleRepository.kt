package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.MerchantRuleRepository
import com.meqlause.fintrack.core.model.MerchantRule
import com.meqlause.fintrack.core.model.MerchantRuleId
import kotlinx.coroutines.flow.Flow

/** ADR-007, FR-088: in-memory merchant rules; the normalized merchant key is unique. */
public class FakeMerchantRuleRepository : MerchantRuleRepository {
    private val store =
        RowStore<MerchantRule>(
            idOf = { it.merchantRuleId.value },
            withId = { rule, newId -> rule.copy(merchantRuleId = MerchantRuleId(newId)) },
            order = compareBy { it.merchantKey },
        )

    override fun observeAll(): Flow<List<MerchantRule>> = store.observe()

    override suspend fun insert(rule: MerchantRule): MerchantRuleId =
        MerchantRuleId(store.append(rule) { existing -> existing.merchantKey == rule.merchantKey })

    override suspend fun findByKey(merchantKey: String): MerchantRule? =
        store.all().firstOrNull { it.merchantKey == merchantKey }

    override suspend fun update(rule: MerchantRule) {
        store.replace(rule)
    }
}
