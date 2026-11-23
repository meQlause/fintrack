package com.meqlause.fintrack.core.domain.loan

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEmpty
import com.meqlause.fintrack.core.domain.transaction.TransactionError
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate

// FR-150, FR-151: the checks a loan draft passes before anything is stored

/** FR-150, FR-151: loan rules tests. */
class LoanRulesTest {
    /** FR-150: a complete draft passes every check. */
    @Test
    fun givenCompleteDraftWhenCheckedThenNoErrorIsReported() {
        assertThat(LoanRules.check(draft(principal = 100_000L))).isEmpty()
    }

    /** FR-151: a zero principal is an amount error. */
    @Test
    fun givenZeroPrincipalWhenCheckedThenAmountRequiredIsReported() {
        assertThat(LoanRules.check(draft(principal = 0L))).containsOnly(TransactionError.AMOUNT_REQUIRED)
    }

    /** FR-151: a principal above the transaction bound is reported as too large. */
    @Test
    fun givenPrincipalAboveBoundWhenCheckedThenAmountTooLargeIsReported() {
        val tooLarge = Money.MAX_TRANSACTION_RUPIAH + 1L
        assertThat(LoanRules.check(draft(principal = tooLarge))).containsOnly(TransactionError.AMOUNT_TOO_LARGE)
    }

    /** FR-151: a blank person name and a missing wallet are both reported. */
    @Test
    fun givenBlankNameAndNoWalletWhenCheckedThenBothErrorsAreReported() {
        val broken = draft(principal = 100_000L, name = "  ", wallet = null)
        assertThat(LoanRules.check(broken)).containsOnly(
            TransactionError.PERSON_REQUIRED,
            TransactionError.WALLET_REQUIRED,
        )
    }

    private fun draft(
        principal: Long,
        name: String = "Andi",
        wallet: WalletId? = WalletId(1L),
    ): LoanDraft =
        LoanDraft(
            direction = LoanDirection.LENT,
            personName = name,
            principal = Money(principal),
            walletId = wallet,
            schedule =
                LoanSchedule(
                    startDate = Instant.parse("2026-11-20T00:00:00Z"),
                    dueDate = LocalDate.of(2026, 12, 20),
                ),
            note = "",
        )
}
