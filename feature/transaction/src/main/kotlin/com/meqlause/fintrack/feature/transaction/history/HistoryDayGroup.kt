package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.model.Money

// FR-036: the transactions of one day, newest first, with the day's net total
internal data class HistoryDayGroup(
    val label: String,
    val net: Money,
    val rows: List<HistoryRowItem>,
)
