package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.WalletId
import java.time.YearMonth

// FR-036, FR-037, S-05: every change the Riwayat screen can ask for
internal sealed interface HistoryEvent {
    data class MonthPicked(
        val month: YearMonth,
    ) : HistoryEvent

    data object PreviousMonth : HistoryEvent

    data object NextMonth : HistoryEvent

    data class YearShifted(
        val delta: Int,
    ) : HistoryEvent

    data class TypePicked(
        val type: HistoryTypeFilter,
    ) : HistoryEvent

    data class WalletPicked(
        val walletId: WalletId?,
    ) : HistoryEvent

    data class CategoryPicked(
        val categoryId: CategoryId?,
    ) : HistoryEvent

    data class DayPicked(
        val day: Int,
    ) : HistoryEvent

    data class QueryChanged(
        val query: String,
    ) : HistoryEvent

    data object SearchToggled : HistoryEvent

    data class SheetOpened(
        val sheet: HistorySheet,
    ) : HistoryEvent

    data object SheetDismissed : HistoryEvent
}
