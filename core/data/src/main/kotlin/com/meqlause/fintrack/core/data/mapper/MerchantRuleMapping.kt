package com.meqlause.fintrack.core.data.mapper

import com.meqlause.fintrack.core.database.entity.MerchantRuleEntity
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.MerchantRule
import com.meqlause.fintrack.core.model.MerchantRuleId
import com.meqlause.fintrack.core.model.WalletId

// DOC-02, FR-088: maps a merchant rule row to the domain model
internal fun MerchantRuleEntity.toMerchantRule(): MerchantRule =
    MerchantRule(
        merchantRuleId = MerchantRuleId(merchantRuleId),
        merchantKey = merchantKey,
        categoryId = CategoryId(categoryId),
        walletId = walletId?.let(::WalletId),
        hitCount = hitCount,
        audit = audit.toAudit(),
    )

// DOC-02, FR-088: maps a merchant rule to its row
internal fun MerchantRule.toEntity(): MerchantRuleEntity =
    MerchantRuleEntity(
        merchantRuleId = merchantRuleId.value,
        merchantKey = merchantKey,
        categoryId = categoryId.value,
        walletId = walletId?.value,
        hitCount = hitCount,
        audit = audit.toColumns(),
    )
