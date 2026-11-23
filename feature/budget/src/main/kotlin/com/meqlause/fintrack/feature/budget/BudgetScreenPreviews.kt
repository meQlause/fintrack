package com.meqlause.fintrack.feature.budget

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.budget.BudgetStatus
import com.meqlause.fintrack.core.model.Budget
import com.meqlause.fintrack.core.model.BudgetId
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import java.time.Instant

// S-11: previews of the list and empty states in light and dark themes
private fun sampleRow(
    id: Long,
    name: String?,
    limit: Long,
    spent: Long,
): BudgetRow {
    val categoryId = name?.let { CategoryId(id) }
    val budget = Budget(BudgetId(id), categoryId, Money(limit), true, Instant.EPOCH, Instant.EPOCH)
    return BudgetRow(BudgetTarget(categoryId, name), BudgetStatus(budget, Money(spent)))
}

private val sampleSuccess =
    BudgetUiState(
        loaded = true,
        overall = sampleRow(1L, null, 5_000_000L, 3_900_000L),
        rows =
            listOf(
                sampleRow(2L, "Makanan", 1_000_000L, 1_100_000L),
                sampleRow(3L, "Transportasi", 500_000L, 400_000L),
                sampleRow(4L, "Belanja", 800_000L, 640_000L),
            ),
    )

@Preview(name = "Anggaran success light", showBackground = true, heightDp = 720)
@Composable
internal fun BudgetSuccessLightPreview() {
    FintrackTheme(darkTheme = false) { BudgetScreen(state = sampleSuccess, callbacks = BudgetCallbacks()) }
}

@Preview(name = "Anggaran success dark", showBackground = true, heightDp = 720)
@Composable
internal fun BudgetSuccessDarkPreview() {
    FintrackTheme(darkTheme = true) { BudgetScreen(state = sampleSuccess, callbacks = BudgetCallbacks()) }
}

@Preview(name = "Anggaran empty light", showBackground = true, heightDp = 720)
@Composable
internal fun BudgetEmptyLightPreview() {
    FintrackTheme(darkTheme = false) {
        BudgetScreen(state = BudgetUiState(loaded = true), callbacks = BudgetCallbacks())
    }
}

@Preview(name = "Anggaran empty dark", showBackground = true, heightDp = 720)
@Composable
internal fun BudgetEmptyDarkPreview() {
    FintrackTheme(darkTheme = true) {
        BudgetScreen(state = BudgetUiState(loaded = true), callbacks = BudgetCallbacks())
    }
}

// NFR-010: font scale 2.0 variant for the screenshot tests
@Preview(name = "Anggaran font 200", showBackground = true, heightDp = 1200, fontScale = 2f)
@Composable
internal fun BudgetFontScalePreview() {
    FintrackTheme(darkTheme = false) { BudgetScreen(state = sampleSuccess, callbacks = BudgetCallbacks()) }
}
