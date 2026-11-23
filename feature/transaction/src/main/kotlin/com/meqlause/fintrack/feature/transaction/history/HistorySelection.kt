package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.model.HistoryView
import com.meqlause.fintrack.core.model.TransactionType
import java.time.YearMonth

// S-05: what the user picked, before it is joined with the stored transactions and the period start day
internal data class HistorySelection(
    val month: YearMonth,
    val filter: HistoryFilter = HistoryFilter(),
    val sheetState: HistorySheetState = HistorySheetState(pickerYear = month.year),
    val statsView: HistoryStatsView = HistoryStatsView(),
) {
    val isSearchOpen: Boolean get() = sheetState.isSearchOpen
    val sheet: HistorySheet? get() = sheetState.sheet
    val pickerYear: Int get() = sheetState.pickerYear
    val view: HistoryView get() = statsView.view
    val statisticsType: TransactionType get() = statsView.statisticsType
    val isCategoriesExpanded: Boolean get() = statsView.isCategoriesExpanded

    // S-05: one change of the search field, the open sheet or the year of the month picker
    fun withSheet(change: HistorySheetState.() -> HistorySheetState): HistorySelection =
        copy(sheetState = sheetState.change())

    // S-05: one change of the view, the statistics type or the expanded category list
    fun withStats(change: HistoryStatsView.() -> HistoryStatsView): HistorySelection =
        copy(statsView = statsView.change())
}

// S-05: the search field, the open sheet and the year the month picker shows
internal data class HistorySheetState(
    val isSearchOpen: Boolean = false,
    val sheet: HistorySheet? = null,
    val pickerYear: Int,
)

// FR-060: the list or the statistics view, the type it totals and whether all categories are shown
internal data class HistoryStatsView(
    val view: HistoryView = HistoryView.LIST,
    val statisticsType: TransactionType = TransactionType.EXPENSE,
    val isCategoriesExpanded: Boolean = false,
)
