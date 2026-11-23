package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType

// FR-036: one transaction line of the list, already reduced to what the row shows
internal data class HistoryRowItem(
    val transactionId: Long,
    val type: TransactionType,
    val amount: Money,
    val title: String?,
    val subtitle: String,
)
