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
    rowId: Long,
    name: String?,
    limit: Long,
    spent: Long,
): BudgetRow {
    val categoryId = name?.let { CategoryId(rowId) }
    val budget = Budget(BudgetId(rowId), categoryId, Money(limit), true, Instant.EPOCH, Instant.EPOCH)
    return BudgetRow(BudgetTarget(categoryId, name), BudgetStatus(budget, Money(spent)))
}

private const val OVERALL_LIMIT = 5_000_000L
private const val OVERALL_SPENT = 3_900_000L
private const val FOOD_LIMIT = 1_000_000L
private const val FOOD_SPENT = 1_100_000L
private const val TRANSPORT_LIMIT = 500_000L
private const val TRANSPORT_SPENT = 400_000L
private const val SHOPPING_LIMIT = 800_000L
private const val SHOPPING_SPENT = 640_000L

private val sampleSuccess =
    BudgetUiState(
        loaded = true,
        overall = sampleRow(rowId = 1L, name = null, limit = OVERALL_LIMIT, spent = OVERALL_SPENT),
        rows =
            listOf(
                sampleRow(rowId = 2L, name = "Makanan", limit = FOOD_LIMIT, spent = FOOD_SPENT),
                sampleRow(rowId = 3L, name = "Transportasi", limit = TRANSPORT_LIMIT, spent = TRANSPORT_SPENT),
                sampleRow(rowId = 4L, name = "Belanja", limit = SHOPPING_LIMIT, spent = SHOPPING_SPENT),
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
