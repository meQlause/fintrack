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
            LoanListRow(
                1L,
                "Andi",
                Money(300_000L),
                0.4f,
                LocalDate.of(2026, 12, 10),
                LoanStatus.OPEN,
                overdue = false,
            ),
            LoanListRow(2L, "Andi", Money(150_000L), 0f, null, LoanStatus.OPEN, overdue = false),
            LoanListRow(3L, "Sari", Money(500_000L), 0.1f, LocalDate.of(2026, 11, 1), LoanStatus.OPEN, overdue = true),
        )
    val settled = LoanListRow(4L, "Budi", Money(0L), 1f, null, LoanStatus.SETTLED, overdue = false)
    return LoanListUiState(direction = LoanDirection.LENT, groups = open.groupedByPerson(), settled = listOf(settled))
}

@Composable
private fun LoanListSample() {
    LoanListScreen(state = sampleState(), onBack = {}, onPickDirection = {}, onOpenLoan = {})
}

@Preview(name = "Loan list light", showBackground = true, heightDp = 720)
@Composable
internal fun LoanListLightPreview() {
    FintrackTheme(darkTheme = false) { LoanListSample() }
}

@Preview(name = "Loan list dark", showBackground = true, heightDp = 720)
@Composable
internal fun LoanListDarkPreview() {
    FintrackTheme(darkTheme = true) { LoanListSample() }
}
