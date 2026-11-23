package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

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
        if (state.isSearchOpen) {
            HistorySearchField(
                query = state.filter.query,
                onQueryChange = { onEvent(HistoryEvent.QueryChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        HistoryMonthStrip(month = state.month, onPick = { onEvent(HistoryEvent.MonthPicked(it)) })
        HistoryTypeTabs(selected = state.filter.type, onPick = { onEvent(HistoryEvent.TypePicked(it)) })
        HistoryFilterChips(state = state, onEvent = onEvent)
        HistoryTotalsStrip(totals = state.totals)
        HistoryMonthContent(state = state, onEvent = onEvent, onOpenTransaction = onOpenTransaction)
    }
    HistorySheetHost(state = state, onEvent = onEvent)
}
