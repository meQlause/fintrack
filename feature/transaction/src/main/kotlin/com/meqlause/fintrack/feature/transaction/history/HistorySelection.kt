package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.model.HistoryView
import com.meqlause.fintrack.core.model.TransactionType
import java.time.YearMonth

// S-05: what the user picked, before it is joined with the stored transactions and the period start day
internal data class HistorySelection(
    val month: YearMonth,
    val filter: HistoryFilter = HistoryFilter(),
    val isSearchOpen: Boolean = false,
    val sheet: HistorySheet? = null,
    val pickerYear: Int = month.year,
    val view: HistoryView = HistoryView.LIST,
    val statisticsType: TransactionType = TransactionType.EXPENSE,
    val isCategoriesExpanded: Boolean = false,
)
