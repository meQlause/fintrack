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
                sampleRow(
                    plan = InstalmentPlanInfo(planId = 1L, title = "HP baru", cardName = "BCA Visa"),
                    tenor = 6,
                    billed = 2,
                    amounts = InstalmentAmounts(Money(rupiah = 500_000L), Money(rupiah = 2_000_000L)),
                ),
                sampleRow(
                    plan = InstalmentPlanInfo(planId = 2L, title = "Sepatu lari", cardName = "Kredivo"),
                    tenor = 3,
                    billed = 1,
                    amounts = InstalmentAmounts(Money(rupiah = 200_000L), Money(rupiah = 400_000L)),
                ),
            ),
        filter = InstalmentFilter.ACTIVE,
    )

private fun sampleRow(
    plan: InstalmentPlanInfo,
    tenor: Int,
    billed: Int,
    amounts: InstalmentAmounts,
): InstalmentRow =
    InstalmentRow(
        plan = plan,
        progress = InstalmentProgress(tenor = tenor, billed = billed, nextMonth = SAMPLE_NEXT, payoffMonth = null),
        sums = amounts,
        portions = emptyList(),
    )

private val SAMPLE_NEXT = YearMonth.parse("2026-12")

@Composable
private fun InstalmentListSample() {
    InstalmentListScreen(
        state = sampleState(),
        callbacks = InstalmentListCallbacks(onBack = {}, onPickFilter = {}, onOpenInstalment = {}),
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
