package com.meqlause.fintrack.core.domain.loan

import com.meqlause.fintrack.core.model.TransactionId

/** FR-152: outcome of recording a repayment; a rejected repayment stores nothing */
public sealed interface RepaymentResult {
    /** FR-152: the repayment is stored under [transactionId]. */
    public data class Saved(
        public val transactionId: TransactionId,
    ) : RepaymentResult

    /** FR-152, BR-041: every rule the repayment breaks. */
    public data class Rejected(
        public val errors: Set<RepaymentError>,
    ) : RepaymentResult
}
