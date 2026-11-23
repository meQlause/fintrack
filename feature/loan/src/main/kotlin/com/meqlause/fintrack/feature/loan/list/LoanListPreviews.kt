package com.meqlause.fintrack.feature.loan.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import java.time.LocalDate

private const val ANDI_OUTSTANDING = 300_000L
private const val DIMAS_OUTSTANDING = 150_000L
private const val SAMPLE_YEAR = 2026
private const val SAMPLE_MONTH = 11
private const val SAMPLE_DAY = 30

// FR-153, S-17: previews of the loan list with two people, one overdue loan and one settled loan
private fun sampleState(): LoanListUiState {
    val dueDate = LocalDate.of(SAMPLE_YEAR, SAMPLE_MONTH, SAMPLE_DAY)
    val open =
        listOf(
            LoanListRow(
                loanId = 1L,
                personName = "Andi",
                outstanding = Money(ANDI_OUTSTANDING),
                progress = 0f,
                due = LoanDue(date = dueDate, overdue = false),
                status = LoanStatus.OPEN,
            ),
            LoanListRow(
                loanId = 2L,
                personName = "Dimas",
                outstanding = Money(DIMAS_OUTSTANDING),
                progress = 0f,
                due = LoanDue(date = null, overdue = false),
                status = LoanStatus.OPEN,
            ),
        )
    return LoanListUiState(direction = LoanDirection.LENT, groups = open.groupedByPerson(), settled = emptyList())
}

@Composable
private fun LoanListSample() {
    val callbacks = LoanListCallbacks(onBack = {}, onPickDirection = {}, onOpenLoan = {})
    LoanListScreen(state = sampleState(), callbacks = callbacks)
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
