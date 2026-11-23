package com.meqlause.fintrack.core.domain.loan

import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Transaction

/** FR-153, BR-041: a loan with the movements that belong to it, the opening one and every repayment, in time order */
public data class LoanEntry(
    public val loan: Loan,
    public val movements: List<Transaction>,
) {
    public val loanBalance: LoanBalance
        get() =
            LoanBalance(
                principal = loan.agreement.principal,
                repaid = Money(movements.filter { it.movement.type.isRepayment() }.sumOf { it.movement.amount.rupiah }),
            )
}
