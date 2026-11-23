package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.MerchantRuleRepository
import com.meqlause.fintrack.core.model.MerchantRule
import com.meqlause.fintrack.core.model.MerchantRuleId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

// FR-088, BR-012: in-memory merchant rules; the normalized merchant key is unique
internal class InMemoryMerchantRuleRepository : MerchantRuleRepository {
    private val rows = MutableStateFlow<List<MerchantRule>>(emptyList())
    private var lastId = 0L

    override fun observeAll(): Flow<List<MerchantRule>> = rows

    override suspend fun insert(rule: MerchantRule): MerchantRuleId {
        require(rows.value.none { it.merchantKey == rule.merchantKey }) { "unique constraint violated" }
        lastId += 1
        val id = MerchantRuleId(lastId)
        rows.value = rows.value + rule.copy(merchantRuleId = id)
        return id
    }

    override suspend fun findByKey(merchantKey: String): MerchantRule? =
        rows.value.firstOrNull { it.merchantKey == merchantKey }

    override suspend fun update(rule: MerchantRule) {
        rows.value = rows.value.map { if (it.merchantRuleId == rule.merchantRuleId) rule else it }
    }
}
