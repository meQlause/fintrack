package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Appearance
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

// S-05, DOC-08: the mockup list of 2026-11 (Hari ini, Kemarin, Jum 20 Nov) with its Masuk, Keluar and Selisih
internal fun sampleHistoryState(): HistoryUiState =
    HistoryUiState(
        month = YearMonth.of(2026, 11),
        groups =
            listOf(
                HistoryDayGroup(
                    label = "Hari ini",
                    netTotal = Money(-73_000L),
                    rows =
                        listOf(
                            sampleRow(1L, "Makan siang", 45_000L, "BCA  ·  12:30", Appearance("food", "orange"), true),
                            sampleRow(2L, "Kopi pagi", 28_000L, "GoPay  ·  08:10", Appearance("food", "orange"), true),
                        ),
                ),
                HistoryDayGroup(
                    label = "Kemarin",
                    netTotal = Money(-204_500L),
                    rows =
                        listOf(
                            sampleRow(
                                3L,
                                "Belanja bulanan",
                                186_500L,
                                "BCA  ·  19:45",
                                Appearance("shopping", "pink"),
                                true,
                            ),
                            sampleRow(
                                4L,
                                "Ojek ke kantor",
                                18_000L,
                                "GoPay  ·  07:50",
                                Appearance("transport", "blue"),
                                true,
                            ),
                        ),
                ),
                HistoryDayGroup(
                    label = "Jum, 20 Nov",
                    netTotal = Money(-79_000L),
                    rows =
                        listOf(
                            sampleRow(
                                5L,
                                "Langganan musik",
                                54_000L,
                                "BCA  ·  20:15",
                                Appearance("entertainment", "violet"),
                            ),
                            sampleRow(6L, "Nasi padang", 25_000L, "Tunai  ·  13:00", Appearance("food", "orange")),
                        ),
                ),
            ),
        totals = HistoryTotals(income = Money(9_700_000L), expense = Money(8_214_500L)),
        hasRecords = true,
        isLoaded = true,
        activeMonths = setOf(YearMonth.of(2026, 10), YearMonth.of(2026, 11)),
    )

private fun sampleRow(
    rowId: Long,
    title: String,
    amount: Long,
    subtitle: String,
    appearance: Appearance,
    auto: Boolean = false,
): HistoryRowItem = HistoryRowItem(rowId, TransactionType.EXPENSE, Money(amount), title, subtitle, appearance, auto)

// NFR-010: font scale 2.0 variant for the screenshot tests
@Preview(name = "History font 200", showBackground = true, fontScale = 2f)
@Composable
internal fun HistoryScreenFontScalePreview() {
    FintrackTheme(darkTheme = false) {
        HistoryScreen(state = sampleHistoryState(), onEvent = {}, onOpenTransaction = {})
    }
}
