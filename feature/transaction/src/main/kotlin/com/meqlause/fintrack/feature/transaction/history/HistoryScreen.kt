package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.HistoryView

// FR-036, FR-037, S-05: stateless Riwayat screen; every change goes out as a HistoryEvent
@Composable
internal fun HistoryScreen(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
    onOpenTransaction: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(FintrackTheme.colors.surfaces.background)
                .padding(horizontal = FintrackTheme.spacing.large, vertical = FintrackTheme.spacing.large),
    ) {
        HistoryHeader(
            onCalendarClick = { onEvent(HistoryEvent.SheetOpened(HistorySheet.MONTH)) },
            onSearchClick = { onEvent(HistoryEvent.SearchToggled) },
        )
        if (state.isSearchOpen && state.view == HistoryView.LIST) {
            HistorySearchField(
                query = state.filter.query,
                onQueryChange = { onEvent(HistoryEvent.QueryChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        HistoryMonthStrip(month = state.month, onPick = { onEvent(HistoryEvent.MonthPicked(it)) })
        HistoryViewSwitch(view = state.view, onPick = { onEvent(HistoryEvent.ViewPicked(it)) })
        HistoryBody(state = state, onEvent = onEvent, onOpenTransaction = onOpenTransaction)
    }
    HistorySheetHost(state = state, onEvent = onEvent)
}

// S-05, S-08: the list of the month, or its statistics when the statistics view is picked
@Composable
private fun ColumnScope.HistoryBody(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
    onOpenTransaction: (Long) -> Unit,
) {
    if (state.view == HistoryView.STATISTICS) {
        StatisticsContent(state = state, onEvent = onEvent)
        return
    }
    HistoryTypeTabs(selected = state.filter.type, onPick = { onEvent(HistoryEvent.TypePicked(it)) })
    HistoryFilterChips(state = state, onEvent = onEvent)
    HistoryTotalsStrip(totals = state.totals)
    HistoryMonthContent(state = state, onEvent = onEvent, onOpenTransaction = onOpenTransaction)
}
