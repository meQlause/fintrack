package com.meqlause.fintrack.feature.credit.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.credit.CreditAccount
import com.meqlause.fintrack.core.domain.credit.CreditEntry
import com.meqlause.fintrack.core.domain.credit.StatementCalculator
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType
import java.time.LocalDate

// FR-131 to FR-138, S-14: previews of the credit wallet detail with a bill that is due, light and dark
private val previewToday: LocalDate = LocalDate.of(2026, 11, 5)

private fun previewState(pay: PayDraft? = null): CreditDetailUiState.Success {
    val terms = CreditTerms(Money(10_000_000L), statementDay = 25, dueDay = 10)
    val account =
        CreditAccount(
            terms = terms,
            initialBalance = Money(0L),
            createdOn = LocalDate.of(2026, 1, 1),
            entries = listOf(CreditEntry(LocalDate.of(2026, 10, 5), Money(-3_500_000L), isTransferIn = false)),
            instalments = emptyList(),
        )
    return CreditDetailUiState.Success(
        name = "BCA Visa",
        type = WalletType.CREDIT_CARD,
        terms = terms,
        statement = StatementCalculator.current(account, previewToday),
        outstanding = StatementCalculator.outstandingOn(account, previewToday),
        availableLimit = StatementCalculator.availableLimit(account, previewToday),
        today = previewToday,
        sources = listOf(PaySource(WalletId(2L), "BCA Tabungan")),
        pay = pay,
        picker = null,
    )
}

@Composable
private fun CreditDetailSample(state: CreditDetailUiState.Success) {
    CreditDetailScreen(state = state, callbacks = CreditDetailCallbacks())
}

@Preview(name = "Credit detail light", showBackground = true, heightDp = 720)
@Composable
internal fun CreditDetailLightPreview() {
    FintrackTheme(darkTheme = false) { CreditDetailSample(previewState()) }
}

@Preview(name = "Credit detail dark", showBackground = true, heightDp = 720)
@Composable
internal fun CreditDetailDarkPreview() {
    FintrackTheme(darkTheme = true) { CreditDetailSample(previewState()) }
}

@Preview(name = "Credit pay sheet light", showBackground = true, heightDp = 720)
@Composable
internal fun CreditPaySheetLightPreview() {
    FintrackTheme(darkTheme = false) {
        CreditDetailSample(previewState(pay = PayDraft(amount = "3500000", sourceId = null, failed = false)))
    }
}
