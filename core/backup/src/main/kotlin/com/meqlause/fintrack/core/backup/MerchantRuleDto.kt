package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.MerchantRule
import com.meqlause.fintrack.core.model.MerchantRuleId
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.serialization.Serializable

// DOC-11: one merchant memory row in the backup; merchantKey is unique
@Serializable
internal data class MerchantRuleDto(
    val merchantRuleId: Long,
    val merchantKey: String,
    val categoryId: Long,
    val walletId: Long?,
    val hitCount: Int,
    val audit: AuditDto,
) {
    fun toModel(): MerchantRule =
        MerchantRule(
            merchantRuleId = MerchantRuleId(merchantRuleId),
            merchantKey = merchantKey,
            categoryId = CategoryId(categoryId),
            walletId = walletId?.let(::WalletId),
            hitCount = hitCount,
            audit = audit.toModel(),
        )

    companion object {
        fun from(rule: MerchantRule): MerchantRuleDto =
            MerchantRuleDto(
                merchantRuleId = rule.merchantRuleId.value,
                merchantKey = rule.merchantKey,
                categoryId = rule.categoryId.value,
                walletId = rule.walletId?.value,
                hitCount = rule.hitCount,
                audit = AuditDto.from(rule.audit),
            )
    }
}
