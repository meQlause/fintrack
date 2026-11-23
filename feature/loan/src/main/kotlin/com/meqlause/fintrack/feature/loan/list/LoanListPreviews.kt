package com.meqlause.fintrack.feature.loan.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import java.time.LocalDate

// FR-153, S-17: previews of the loan list with two people, one overdue loan and one settled loan
private fun sampleState(): LoanListUiState {
    val open =
        listOf(
            LoanListRow(1L, "Andi", Money(300_000L), 0f, LocalDate.of(2026, 11, 30), LoanStatus.OPEN, overdue = false),
            LoanListRow(3L, "Dimas", Money(150_000L), 0f, null, LoanStatus.OPEN, overdue = false),
        )
    return LoanListUiState(direction = LoanDirection.LENT, groups = open.groupedByPerson(), settled = emptyList())
}

@Composable
private fun LoanListSample() {
    LoanListScreen(state = sampleState(), onBack = {}, onPickDirection = {}, onOpenLoan = {})
}

@Preview(name = "Loan list light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun LoanListLightPreview() {
    FintrackTheme(darkTheme = false) { LoanListSample() }
}

@Preview(name = "Loan list dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun LoanListDarkPreview() {
    FintrackTheme(darkTheme = true) { LoanListSample() }
}
