package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import java.time.YearMonth

// S-05, DOC-08: the Riwayat of a month without transactions, the receipt art and "+ Catat", no summary card
@Preview(name = "History empty light", showBackground = true)
@Composable
internal fun HistoryEmptyLightPreview() {
    FintrackTheme(darkTheme = false) {
        HistoryScreen(
            state = emptyHistoryState(),
            onEvent = {},
            links = HistoryLinks(onOpenTransaction = {}, onAdd = {}),
        )
    }
}

private fun emptyHistoryState(): HistoryUiState =
    HistoryUiState(
        selection = HistorySelection(month = YearMonth.parse("2026-06")),
        status = HistoryStatus(isLoaded = true),
    )
