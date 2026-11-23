package com.meqlause.fintrack.core.domain.statistics

import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money

/** FR-060, FR-062: one category's amount in the period and in the period before it; a null id means no category. */
public data class CategoryChange(
    public val categoryId: CategoryId?,
    public val amount: Money,
    public val previousAmount: Money,
) {
    /** FR-062: change against the previous period in whole percent; null when the previous amount is zero. */
    public val percentChange: Long?
        get() = percentChangeOf(amount, previousAmount)
}
