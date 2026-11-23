package com.meqlause.fintrack.core.domain.transaction

import com.meqlause.fintrack.core.model.TransactionId

/** FR-030: outcome of saving the editor sheet; a rejected draft stores nothing. */
public sealed interface SaveTransactionResult {
    /** FR-030, FR-034: the transaction is stored under [transactionId]. */
    public data class Saved(
        public val transactionId: TransactionId,
    ) : SaveTransactionResult

    /** BR-001..BR-009, BR-039: every rule the draft breaks. */
    public data class Rejected(
        public val errors: Set<TransactionError>,
    ) : SaveTransactionResult
}
