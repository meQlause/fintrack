package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.model.Money

// FR-137, FR-146, S-03: the Tagihan card content, the credit bills and the instalments due this period
internal data class HomeBills(
    val bills: List<HomeBill>,
    val instalmentsDue: Money,
)
