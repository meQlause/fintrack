package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Immutable
import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.HistoryView
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import java.time.LocalDate
import java.time.YearMonth

// FR-036, FR-037, S-05: everything the Riwayat screen draws; the screen holds no state of its own
@Immutable
internal data class HistoryUiState(
    val selection: HistorySelection,
    val period: Period? = null,
    val lists: HistoryLists = HistoryLists(),
    val status: HistoryStatus = HistoryStatus(),
    val statistics: PeriodStatistics? = null,
) {
    val month: YearMonth get() = selection.month
    val filter: HistoryFilter get() = selection.filter
    val isSearchOpen: Boolean get() = selection.isSearchOpen
    val sheet: HistorySheet? get() = selection.sheet
    val pickerYear: Int get() = selection.pickerYear
    val view: HistoryView get() = selection.view
    val statisticsType: TransactionType get() = selection.statisticsType
    val isCategoriesExpanded: Boolean get() = selection.isCategoriesExpanded
    val wallets: List<Wallet> get() = lists.wallets
    val categories: List<Category> get() = lists.categories
    val groups: List<HistoryDayGroup> get() = lists.groups
    val totals: HistoryTotals get() = status.totals
    val hasRecords: Boolean get() = status.hasRecords
    val isLoaded: Boolean get() = status.isLoaded
    val activeMonths: Set<YearMonth> get() = status.activeMonths
    val activeDays: Set<LocalDate> get() = status.days.activeDays
    val today: LocalDate? get() = status.days.today
    val todayIndex: Int? get() = status.todayIndex
}

// FR-036: the wallets and categories the filters offer and the day groups of the list
@Immutable
internal data class HistoryLists(
    val wallets: List<Wallet> = emptyList(),
    val categories: List<Category> = emptyList(),
    val groups: List<HistoryDayGroup> = emptyList(),
)

// S-05: the calendar days that have transactions and the day it is today, for the period picker
@Immutable
internal data class HistoryDays(
    val activeDays: Set<LocalDate> = emptySet(),
    val today: LocalDate? = null,
)

// FR-036: the month totals, whether it has records or loaded, its active months and days, today and where it falls
@Immutable
internal data class HistoryStatus(
    val totals: HistoryTotals = HistoryTotals(),
    val hasRecords: Boolean = false,
    val isLoaded: Boolean = false,
    val activeMonths: Set<YearMonth> = emptySet(),
    val days: HistoryDays = HistoryDays(),
    val todayIndex: Int? = null,
)
