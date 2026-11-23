package com.meqlause.fintrack.feature.loan.detail

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.loan.LoanEntry
import com.meqlause.fintrack.core.domain.loan.RepaymentError
import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId
import org.junit.Test
import java.time.Instant

// FR-152, FR-155: the form and the screen state answer what the user may do with the loan
class LoanDetailStateTest {
    private val nowStamp = Instant.parse("2026-11-23T07:05:00Z")

    private fun entry(
        status: LoanStatus,
        direction: LoanDirection,
    ): LoanEntry =
        LoanEntry(
            loan =
                Loan(
                    loanId = LoanId(1L),
                    agreement =
                        Agreement(
                            direction = direction,
                            personName = "Budi",
                            principal = Money(300_000L),
                            startDate = nowStamp,
                            dueDate = null,
                            note = "",
                        ),
                    status = status,
                    closedAt = null,
                    audit = Audit(nowStamp, nowStamp),
                ),
            movements = emptyList(),
        )

    @Test
    fun givenAnOpenPiutangWhenAskedThenRepaymentAndWriteOffAreAllowed() {
        val state = LoanDetailUiState(entry = entry(LoanStatus.OPEN, LoanDirection.LENT), isLoaded = true)

        assertThat(state.canRepay).isTrue()
        assertThat(state.canWriteOff).isTrue()
    }

    @Test
    fun givenAnOpenHutangWhenAskedThenOnlyRepaymentIsAllowed() {
        val state = LoanDetailUiState(entry = entry(LoanStatus.OPEN, LoanDirection.BORROWED), isLoaded = true)

        assertThat(state.canRepay).isTrue()
        assertThat(state.canWriteOff).isFalse()
    }

    @Test
    fun givenASettledLoanWhenAskedThenNothingIsAllowed() {
        val state = LoanDetailUiState(entry = entry(LoanStatus.SETTLED, LoanDirection.LENT), isLoaded = true)

        assertThat(state.canRepay).isFalse()
        assertThat(state.canWriteOff).isFalse()
    }

    @Test
    fun givenNoLoanWhenAskedThenNothingIsAllowed() {
        val state = LoanDetailUiState()

        assertThat(state.canRepay).isFalse()
        assertThat(state.canWriteOff).isFalse()
    }

    @Test
    fun givenDigitsWhenAmountIsReadThenItIsTheRupiahValue() {
        assertThat(RepaymentForm(rupiahDigits = "15000").amount).isEqualTo(Money(15_000L))
        assertThat(RepaymentForm(rupiahDigits = "").amount).isEqualTo(Money(0L))
    }

    @Test
    fun givenTwoFormsWithTheSameValuesWhenComparedThenTheyAreEqual() {
        val first =
            RepaymentForm(rupiahDigits = "100", walletId = WalletId(2L), errors = setOf(RepaymentError.WALLET_REQUIRED))
        val same = first.copy()
        val other = first.copy(rupiahDigits = "200")

        assertThat(first).isEqualTo(same)
        assertThat(first.hashCode()).isEqualTo(same.hashCode())
        assertThat(first).isNotEqualTo(other)
    }

    @Test
    fun givenAnOverlayWhenNothingIsOpenThenItHasNoSheetOrDialog() {
        val overlay = LoanOverlay()

        assertThat(overlay.sheet).isEqualTo(null)
        assertThat(overlay.dialog).isEqualTo(null)
        assertThat(overlay).isEqualTo(LoanOverlay(sheet = null, dialog = null))
    }
}
