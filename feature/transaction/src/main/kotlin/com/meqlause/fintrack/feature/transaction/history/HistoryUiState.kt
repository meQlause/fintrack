package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Immutable
import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.HistoryView
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import java.time.YearMonth

// FR-036, FR-037, S-05: everything the Riwayat screen draws; the screen holds no state of its own
@Immutable
internal data class HistoryUiState(
    val month: YearMonth,
    val period: Period? = null,
    val filter: HistoryFilter = HistoryFilter(),
    val isSearchOpen: Boolean = false,
    val sheet: HistorySheet? = null,
    val pickerYear: Int = month.year,
    val wallets: List<Wallet> = emptyList(),
    val categories: List<Category> = emptyList(),
    val groups: List<HistoryDayGroup> = emptyList(),
    val totals: HistoryTotals = HistoryTotals(),
    val hasRecords: Boolean = false,
    val isLoaded: Boolean = false,
    val view: HistoryView = HistoryView.LIST,
    val statisticsType: TransactionType = TransactionType.EXPENSE,
    val isCategoriesExpanded: Boolean = false,
    val statistics: PeriodStatistics? = null,
)
