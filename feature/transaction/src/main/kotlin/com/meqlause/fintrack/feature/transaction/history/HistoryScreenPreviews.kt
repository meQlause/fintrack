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
        HistoryScreen(
            state = sampleHistoryState(),
            onEvent = {},
            links = HistoryLinks(onOpenTransaction = {}, onAdd = {}),
        )
    }
}

@Preview(name = "History dark", showBackground = true)
@Composable
internal fun HistoryScreenDarkPreview() {
    FintrackTheme(darkTheme = true) {
        HistoryScreen(
            state = sampleHistoryState(),
            onEvent = {},
            links = HistoryLinks(onOpenTransaction = {}, onAdd = {}),
        )
    }
}

// S-05, DOC-08: the mockup list of 2026-11 (Hari ini, Kemarin, Jum 20 Nov) with its Masuk, Keluar and Selisih
internal fun sampleHistoryState(): HistoryUiState =
    HistoryUiState(
        selection = HistorySelection(month = YearMonth.parse("2026-11")),
        lists = HistoryLists(groups = SAMPLE_GROUPS),
        status =
            HistoryStatus(
                totals = SAMPLE_TOTALS,
                hasRecords = true,
                isLoaded = true,
                activeMonths = SAMPLE_MONTHS,
            ),
    )

private val SAMPLE_TOTALS = HistoryTotals(income = Money(rupiah = 9_700_000L), expense = Money(rupiah = 8_214_500L))

private val SAMPLE_MONTHS = setOf(YearMonth.parse("2026-10"), YearMonth.parse("2026-11"))

private val FOOD_LOOK = Appearance("food", "orange")

private val SAMPLE_GROUPS =
    listOf(
        HistoryDayGroup(
            label = "Hari ini",
            netTotal = Money(rupiah = -73_000L),
            rows =
                listOf(
                    sampleRow(
                        SampleRow(rowId = 1L, title = "Makan siang", amount = 45_000L),
                        SampleLook("BCA  ·  12:30", FOOD_LOOK, true),
                    ),
                    sampleRow(
                        SampleRow(rowId = 2L, title = "Kopi pagi", amount = 28_000L),
                        SampleLook("GoPay  ·  08:10", FOOD_LOOK, true),
                    ),
                ),
        ),
        HistoryDayGroup(
            label = "Kemarin",
            netTotal = Money(rupiah = -204_500L),
            rows =
                listOf(
                    sampleRow(
                        SampleRow(rowId = 3L, title = "Belanja bulanan", amount = 186_500L),
                        SampleLook("BCA  ·  19:45", Appearance("shopping", "pink"), true),
                    ),
                    sampleRow(
                        SampleRow(rowId = 4L, title = "Ojek ke kantor", amount = 18_000L),
                        SampleLook("GoPay  ·  07:50", Appearance("transport", "blue"), true),
                    ),
                ),
        ),
        HistoryDayGroup(
            label = "Jum, 20 Nov",
            netTotal = Money(rupiah = -79_000L),
            rows =
                listOf(
                    sampleRow(
                        SampleRow(rowId = 5L, title = "Langganan musik", amount = 54_000L),
                        SampleLook("BCA  ·  20:15", Appearance("entertainment", "violet")),
                    ),
                    sampleRow(
                        SampleRow(rowId = 6L, title = "Nasi padang", amount = 25_000L),
                        SampleLook("Tunai  ·  13:00", FOOD_LOOK),
                    ),
                ),
        ),
    )

// S-05: the id, title and amount of one sample row
private data class SampleRow(
    val rowId: Long,
    val title: String,
    val amount: Long,
)

// S-05: the subtitle, icon and whether the row came from a detected notification
private data class SampleLook(
    val subtitle: String,
    val appearance: Appearance,
    val auto: Boolean = false,
)

private fun sampleRow(
    sample: SampleRow,
    look: SampleLook,
): HistoryRowItem =
    HistoryRowItem(
        transactionId = sample.rowId,
        type = TransactionType.EXPENSE,
        amount = Money(sample.amount),
        title = sample.title,
        subtitle = look.subtitle,
        mark = HistoryRowMark(appearance = look.appearance, auto = look.auto),
    )

// NFR-010: font scale 2.0 variant for the screenshot tests
@Preview(name = "History font 200", showBackground = true, fontScale = 2f)
@Composable
internal fun HistoryScreenFontScalePreview() {
    FintrackTheme(darkTheme = false) {
        HistoryScreen(
            state = sampleHistoryState(),
            onEvent = {},
            links = HistoryLinks(onOpenTransaction = {}, onAdd = {}),
        )
    }
}
