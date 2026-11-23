package com.meqlause.fintrack.core.domain.inbox

import com.meqlause.fintrack.core.model.TransactionId

/** FR-087: outcome of confirming an inbox item; nothing is stored unless the result is [Confirmed]. */
public sealed interface ConfirmInboxResult {
    /** BR-011: the transaction created from the item. */
    public data class Confirmed(
        public val transactionId: TransactionId,
    ) : ConfirmInboxResult

    /** FR-087: the item is unknown or was already resolved. */
    public data object NotPending : ConfirmInboxResult

    /** FR-087: a source or target wallet is required before confirming. */
    public data object MissingWallet : ConfirmInboxResult

    /** FR-087: a category is required to confirm a single expense or income. */
    public data object MissingCategory : ConfirmInboxResult
}
