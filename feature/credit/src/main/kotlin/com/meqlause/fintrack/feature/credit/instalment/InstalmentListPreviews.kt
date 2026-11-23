package com.meqlause.fintrack.feature.credit.instalment

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import java.time.YearMonth

// FR-142, S-16: previews of the instalment list with two active plans, light and dark
private fun sampleState(): InstalmentListUiState.Success =
    InstalmentListUiState.Success(
        rows =
            listOf(
                InstalmentRow(
                    planId = 1L,
                    title = "HP baru",
                    cardName = "BCA Visa",
                    tenor = 6,
                    billed = 2,
                    monthly = Money(500_000L),
                    remaining = Money(2_000_000L),
                    nextMonth = YearMonth.of(2026, 12),
                    payoffMonth = null,
                    portions = emptyList(),
                ),
                InstalmentRow(
                    planId = 2L,
                    title = "Sepatu lari",
                    cardName = "Kredivo",
                    tenor = 3,
                    billed = 1,
                    monthly = Money(200_000L),
                    remaining = Money(400_000L),
                    nextMonth = YearMonth.of(2026, 12),
                    payoffMonth = null,
                    portions = emptyList(),
                ),
            ),
        filter = InstalmentFilter.ACTIVE,
    )

@Composable
private fun InstalmentListSample() {
    InstalmentListScreen(
        state = sampleState(),
        onBack = {},
        onPickFilter = {},
        onOpenInstalment = {},
    )
}

@Preview(name = "Instalment list light", showBackground = true, heightDp = 720)
@Composable
internal fun InstalmentListLightPreview() {
    FintrackTheme(darkTheme = false) { InstalmentListSample() }
}

@Preview(name = "Instalment list dark", showBackground = true, heightDp = 720)
@Composable
internal fun InstalmentListDarkPreview() {
    FintrackTheme(darkTheme = true) { InstalmentListSample() }
}
