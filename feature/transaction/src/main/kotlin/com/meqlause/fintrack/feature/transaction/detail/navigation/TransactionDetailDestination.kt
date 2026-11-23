package com.meqlause.fintrack.feature.transaction.detail.navigation

import kotlinx.serialization.Serializable

// FR-034, FR-035, S-06: the transaction detail route; the transaction id is the only argument
@Serializable
public data class TransactionDetailDestination(
    public val transactionId: Long,
)
