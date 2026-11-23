package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Immutable
import com.meqlause.fintrack.core.model.Money

// FR-036: the transactions of one day, newest first, with the day's net total
@Immutable
internal data class HistoryDayGroup(
    val label: String,
    val netTotal: Money,
    val rows: List<HistoryRowItem>,
)
