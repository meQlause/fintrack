package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.model.Money

// FR-042: one expense category of the current period; name is null for expenses without a category
internal data class HomeCategoryShare(
    val name: String?,
    val amount: Money,
    val count: Int,
    val colorKey: String = "gray",
)
