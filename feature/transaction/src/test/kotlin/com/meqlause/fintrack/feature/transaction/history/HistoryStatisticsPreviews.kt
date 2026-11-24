package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.statistics.CategoryChange
import com.meqlause.fintrack.core.domain.statistics.PeriodStatistics
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.HistoryView
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import java.time.Instant
import java.time.YearMonth

// FR-060..062, S-08: light preview of the statistics view of Riwayat
@Preview(name = "Statistics light", showBackground = true)
@Composable
internal fun HistoryStatisticsLightPreview() {
    FintrackTheme(darkTheme = false) {
        HistoryScreen(
            state = sampleStatisticsState(),
            onEvent = {},
            links = HistoryLinks(onOpenTransaction = {}, onAdd = {}),
        )
    }
}

// FR-060..062, S-08: dark preview of the statistics view of Riwayat
@Preview(name = "Statistics dark", showBackground = true)
@Composable
internal fun HistoryStatisticsDarkPreview() {
    FintrackTheme(darkTheme = true) {
        HistoryScreen(
            state = sampleStatisticsState(),
            onEvent = {},
            links = HistoryLinks(onOpenTransaction = {}, onAdd = {}),
        )
    }
}

// FR-060..062, DOC-08: the mockup breakdown of 2026-11 against October: nine expense categories and 22 days
internal fun sampleStatisticsState(): HistoryUiState =
    HistoryUiState(
        selection =
            HistorySelection(
                month = YearMonth.of(2026, 11),
                statsView = HistoryStatsView(view = HistoryView.STATISTICS),
            ),
        lists =
            HistoryLists(
                categories =
                    SAMPLE_CATEGORIES.mapIndexed {
                        index,
                        row,
                        ->
                        sampleCategory(index + 1L, row.name, row.icon, row.color)
                    },
            ),
        status = HistoryStatus(hasRecords = true, isLoaded = true),
        statistics =
            PeriodStatistics(
                type = TransactionType.EXPENSE,
                total = Money(8_214_500L),
                previousTotal = Money(5_134_000L),
                categories =
                    SAMPLE_CATEGORIES.mapIndexed { index, row ->
                        CategoryChange(CategoryId(index + 1L), Money(row.amount), Money(row.previous))
                    },
                dailyTotals = List(22) { day -> Money(if (day % 4 == 3) 0L else (day * 37_000L) % 420_000L + 60_000L) },
            ),
    )

private data class SampleCategory(
    val name: String,
    val icon: String,
    val color: String,
    val amount: Long,
    val previous: Long,
)

private val SAMPLE_CATEGORIES =
    listOf(
        SampleCategory("Belanja", "shopping", "pink", 4_175_500L, 1_906_600L),
        SampleCategory("Tagihan", "bill", "purple", 2_300_000L, 1_916_700L),
        SampleCategory("Makanan & Minuman", "food", "orange", 869_000L, 583_200L),
        SampleCategory("Transportasi", "transport", "blue", 300_000L, 250_000L),
        SampleCategory("Hiburan", "entertainment", "violet", 219_000L, 180_000L),
        SampleCategory("Sosial & Kondangan", "gift", "rose", 200_000L, 150_000L),
        SampleCategory("Pulsa & Internet", "phone", "cyan", 105_000L, 105_000L),
        SampleCategory("Kesehatan", "health", "red", 45_000L, 40_000L),
        SampleCategory("Lainnya", "other", "gray", 1_000L, 2_500L),
    )

private fun sampleCategory(
    categoryNumber: Long,
    name: String,
    icon: String,
    color: String,
): Category =
    Category(
        categoryId = CategoryId(categoryNumber),
        profile =
            CategoryProfile(
                name = name,
                kind = CategoryKind.EXPENSE,
                appearance = Appearance(iconKey = icon, colorKey = color),
                sortOrder = categoryNumber.toInt(),
            ),
        state = CategoryState(isDefault = true, isArchived = false),
        audit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH),
    )
