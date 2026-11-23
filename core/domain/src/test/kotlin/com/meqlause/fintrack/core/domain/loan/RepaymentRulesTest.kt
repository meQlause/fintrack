package com.meqlause.fintrack.core.domain.loan

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEmpty
import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId
import org.junit.jupiter.api.Test
import java.time.Instant

// FR-152, BR-041, BR-042: the checks a repayment passes before it is stored

/** FR-152, BR-041: repayment rules tests. */
class RepaymentRulesTest {
    private val open = entry(LoanStatus.OPEN, principal = 100_000L)
    private val wallet = WalletId(1L)

    /** FR-152: a valid amount inside the outstanding balance passes every check. */
    @Test
    fun givenValidRepaymentWhenCheckedThenNoErrorIsReported() {
        assertThat(RepaymentRules.check(Money(40_000L), wallet, open)).isEmpty()
    }

    /** FR-152: a zero amount is an amount error. */
    @Test
    fun givenZeroAmountWhenCheckedThenAmountRequiredIsReported() {
        assertThat(RepaymentRules.check(Money(0L), wallet, open)).containsOnly(RepaymentError.AMOUNT_REQUIRED)
    }

    /** FR-152: a repayment without a wallet is refused. */
    @Test
    fun givenNoWalletWhenCheckedThenWalletRequiredIsReported() {
        assertThat(RepaymentRules.check(Money(40_000L), null, open)).containsOnly(RepaymentError.WALLET_REQUIRED)
    }

    /** BR-042: a settled or written off loan accepts no repayment. */
    @Test
    fun givenClosedLoanWhenCheckedThenLoanClosedIsReported() {
        val settled = entry(LoanStatus.SETTLED, principal = 100_000L)
        assertThat(RepaymentRules.check(Money(40_000L), wallet, settled)).containsOnly(RepaymentError.LOAN_CLOSED)
    }

    /** BR-041: paying more than the outstanding balance is refused. */
    @Test
    fun givenAmountAboveOutstandingWhenCheckedThenAboveOutstandingIsReported() {
        val result = RepaymentRules.check(Money(100_001L), wallet, open)
        assertThat(result).containsOnly(RepaymentError.AMOUNT_ABOVE_OUTSTANDING)
    }

    private fun entry(
        status: LoanStatus,
        principal: Long,
    ): LoanEntry =
        LoanEntry(
            loan =
                Loan(
                    loanId = LoanId(1L),
                    agreement =
                        Agreement(
                            direction = LoanDirection.LENT,
                            personName = "Andi",
                            principal = Money(principal),
                            startDate = Instant.parse("2026-11-20T00:00:00Z"),
                            dueDate = null,
                            note = "",
                        ),
                    status = status,
                    closedAt = null,
                    audit = Audit(Instant.EPOCH, Instant.EPOCH),
                ),
            movements = emptyList(),
        )
}
