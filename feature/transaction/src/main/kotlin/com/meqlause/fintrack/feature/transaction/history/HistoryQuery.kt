package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.domain.period.Period

// FR-036, FR-037: which transactions the list keeps, those that pass the filter inside the period
internal data class HistoryQuery(
    val filter: HistoryFilter,
    val period: Period,
)
