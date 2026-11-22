package com.meqlause.fintrack.core.model

import java.time.Instant

/** DOC-02: a loan to or from a person (FR-150 to FR-157, ADR-016). Repayments are transactions with its loanId. */
public data class Loan(
    public val loanId: LoanId,
    public val agreement: Agreement,
    public val status: LoanStatus,
    public val closedAt: Instant?,
    public val audit: Audit,
)
