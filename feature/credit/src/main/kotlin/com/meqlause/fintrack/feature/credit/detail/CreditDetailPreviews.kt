package com.meqlause.fintrack.feature.credit.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.credit.StatementAmounts
import com.meqlause.fintrack.core.domain.credit.StatementPeriod
import com.meqlause.fintrack.core.domain.credit.StatementSummary
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.StatementStatus
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.feature.credit.instalment.InstalmentAmounts
import com.meqlause.fintrack.feature.credit.instalment.InstalmentPlanInfo
import com.meqlause.fintrack.feature.credit.instalment.InstalmentProgress
import com.meqlause.fintrack.feature.credit.instalment.InstalmentRow
import java.time.LocalDate
import java.time.YearMonth

// FR-131 to FR-138, S-14: previews of the credit wallet detail with a bill that is due, light and dark
private val previewToday: LocalDate = LocalDate.parse("2026-11-22")

// design/screens/credit.png: BCA Card of the prototype seed on 2026-11-22
private fun previewState(payDraft: PayDraft? = null): CreditDetailUiState.Success {
    val terms = CreditTerms(Money(rupiah = 10_000_000L), statementDay = 15, dueDay = 1)
    val period =
        StatementPeriod(
            month = YearMonth.parse("2026-11"),
            cutOff = LocalDate.parse("2026-11-15"),
            periodStart = LocalDate.parse("2026-10-16"),
            dueDate = LocalDate.parse("2026-12-01"),
        )
    val amounts = StatementAmounts(Money(rupiah = 4_300_000L), Money(rupiah = 2_500_000L), Money(rupiah = 500_000L))
    return CreditDetailUiState.Success(
        identity = CreditIdentity(name = "BCA Card", type = WalletType.CREDIT_CARD),
        figures =
            CreditFigures(
                terms = terms,
                statement = StatementSummary(period, amounts, StatementStatus.DUE),
                outstanding = Money(rupiah = 4_220_000L),
                availableLimit = Money(rupiah = 5_780_000L),
            ),
        today = previewToday,
        payment = PayState(sources = listOf(PaySource(WalletId(2L), "BCA")), payDraft = payDraft),
        picker = null,
        instalments = listOf(previewInstalment()),
    )
}

private fun previewInstalment(): InstalmentRow =
    InstalmentRow(
        plan = InstalmentPlanInfo(planId = 1L, title = "HP baru", cardName = "BCA Card"),
        progress =
            InstalmentProgress(
                tenor = 6,
                billed = 1,
                nextMonth = YearMonth.parse("2026-12"),
                payoffMonth = null,
            ),
        sums = InstalmentAmounts(monthly = Money(rupiah = 500_000L), remaining = Money(rupiah = 2_500_000L)),
        portions = emptyList(),
    )

@Composable
private fun CreditDetailSample(state: CreditDetailUiState.Success) {
    CreditDetailScreen(state = state, callbacks = CreditDetailCallbacks())
}

@Preview(name = "Credit detail light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun CreditDetailLightPreview() {
    FintrackTheme(darkTheme = false) { CreditDetailSample(previewState()) }
}

@Preview(name = "Credit detail dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun CreditDetailDarkPreview() {
    FintrackTheme(darkTheme = true) { CreditDetailSample(previewState()) }
}

@Preview(name = "Credit pay sheet light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun CreditPaySheetLightPreview() {
    FintrackTheme(darkTheme = false) {
        CreditDetailSample(previewState(payDraft = PayDraft(typedDigits = "3500000", sourceId = null, failed = false)))
    }
}
