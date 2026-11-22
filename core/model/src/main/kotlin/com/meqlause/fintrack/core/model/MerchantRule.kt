package com.meqlause.fintrack.core.model

/** DOC-02: merchant memory that suggests a category and wallet (FR-088, BR-012). merchantKey is unique. */
public data class MerchantRule(
    public val merchantRuleId: MerchantRuleId,
    public val merchantKey: String,
    public val categoryId: CategoryId,
    public val walletId: WalletId?,
    public val hitCount: Int,
    public val audit: Audit,
)
