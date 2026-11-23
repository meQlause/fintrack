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
        HistoryScreen(state = sampleStatisticsState(), onEvent = {}, onOpenTransaction = {})
    }
}

// FR-060..062, S-08: dark preview of the statistics view of Riwayat
@Preview(name = "Statistics dark", showBackground = true)
@Composable
internal fun HistoryStatisticsDarkPreview() {
    FintrackTheme(darkTheme = true) {
        HistoryScreen(state = sampleStatisticsState(), onEvent = {}, onOpenTransaction = {})
    }
}

// FR-060..062: a November expense breakdown compared with October, with four categories and a daily sum per day
internal fun sampleStatisticsState(): HistoryUiState =
    HistoryUiState(
        month = YearMonth.of(2026, 11),
        view = HistoryView.STATISTICS,
        categories =
            listOf(
                sampleCategory(1L, "Makanan"),
                sampleCategory(2L, "Transportasi"),
                sampleCategory(3L, "Belanja"),
                sampleCategory(4L, "Hiburan"),
            ),
        statistics =
            PeriodStatistics(
                type = TransactionType.EXPENSE,
                total = Money(1_530_000L),
                previousTotal = Money(1_200_000L),
                categories =
                    listOf(
                        CategoryChange(CategoryId(1L), Money(620_000L), Money(540_000L)),
                        CategoryChange(CategoryId(3L), Money(450_000L), Money(300_000L)),
                        CategoryChange(CategoryId(2L), Money(310_000L), Money(260_000L)),
                        CategoryChange(CategoryId(4L), Money(150_000L), Money(100_000L)),
                    ),
                dailyTotals = List(30) { day -> Money(if (day % 3 == 0) 0L else (day * 37_000L) % 150_000L + 20_000L) },
            ),
        hasRecords = true,
        isLoaded = true,
    )

private fun sampleCategory(
    id: Long,
    name: String,
): Category =
    Category(
        categoryId = CategoryId(id),
        profile =
            CategoryProfile(
                name = name,
                kind = CategoryKind.EXPENSE,
                appearance = Appearance(iconKey = "food", colorKey = "orange"),
                sortOrder = id.toInt(),
            ),
        state = CategoryState(isDefault = true, isArchived = false),
        audit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH),
    )
