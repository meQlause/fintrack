package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import java.time.YearMonth

// FR-036, FR-037, S-05: light and dark previews of the Riwayat screen with sample rows
@Preview(name = "History light", showBackground = true)
@Composable
internal fun HistoryScreenLightPreview() {
    FintrackTheme(darkTheme = false) {
        HistoryScreen(state = sampleHistoryState(), onEvent = {}, onOpenTransaction = {})
    }
}

@Preview(name = "History dark", showBackground = true)
@Composable
internal fun HistoryScreenDarkPreview() {
    FintrackTheme(darkTheme = true) {
        HistoryScreen(state = sampleHistoryState(), onEvent = {}, onOpenTransaction = {})
    }
}

// S-05: a November list with one expense, one transfer and one income day
internal fun sampleHistoryState(): HistoryUiState =
    HistoryUiState(
        month = YearMonth.of(2026, 11),
        groups =
            listOf(
                HistoryDayGroup(
                    label = "Hari ini",
                    netTotal = Money(-175_000L),
                    rows =
                        listOf(
                            HistoryRowItem(
                                1L,
                                TransactionType.EXPENSE,
                                Money(25_000L),
                                "Warung Bu Tini",
                                "BCA · 12:30",
                            ),
                            HistoryRowItem(2L, TransactionType.TRANSFER, Money(150_000L), null, "BCA → GoPay · 09:10"),
                        ),
                ),
                HistoryDayGroup(
                    label = "Kemarin",
                    netTotal = Money(5_000_000L),
                    rows =
                        listOf(
                            HistoryRowItem(3L, TransactionType.INCOME, Money(5_000_000L), "Gaji", "BCA · 08:00"),
                        ),
                ),
            ),
        totals = HistoryTotals(income = Money(5_000_000L), expense = Money(25_000L)),
        hasRecords = true,
        isLoaded = true,
    )

// NFR-010: font scale 2.0 variant for the screenshot tests
@Preview(name = "History font 200", showBackground = true, fontScale = 2f)
@Composable
internal fun HistoryScreenFontScalePreview() {
    FintrackTheme(darkTheme = false) {
        HistoryScreen(state = sampleHistoryState(), onEvent = {}, onOpenTransaction = {})
    }
}
