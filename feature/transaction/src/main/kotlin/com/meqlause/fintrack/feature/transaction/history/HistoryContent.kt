package com.meqlause.fintrack.feature.transaction.history

// FR-036, FR-037: the result of filtering one month, ready for the list and the summary strip
internal data class HistoryContent(
    val groups: List<HistoryDayGroup>,
    val totals: HistoryTotals,
    val hasRecords: Boolean,
)
