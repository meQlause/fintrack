package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.mapper.toEntity
import com.meqlause.fintrack.core.data.mapper.toMerchantRule
import com.meqlause.fintrack.core.database.dao.MerchantRuleDao
import com.meqlause.fintrack.core.domain.repository.MerchantRuleRepository
import com.meqlause.fintrack.core.model.MerchantRule
import com.meqlause.fintrack.core.model.MerchantRuleId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** DOC-03, ADR-007, FR-088: merchant memory in Room; the normalized merchant key is unique. */
public class LocalMerchantRuleRepository(
    private val merchantRuleDao: MerchantRuleDao,
) : MerchantRuleRepository {
    override fun observeAll(): Flow<List<MerchantRule>> =
        merchantRuleDao.observeAll().map { rows -> rows.map { it.toMerchantRule() } }

    override suspend fun insert(rule: MerchantRule): MerchantRuleId =
        MerchantRuleId(merchantRuleDao.insert(rule.toEntity()))
}
