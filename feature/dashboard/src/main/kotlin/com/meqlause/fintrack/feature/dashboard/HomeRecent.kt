package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionType

// FR-043: one of the five most recent transactions; title is null when the transaction has no text at all
internal data class HomeRecent(
    val transactionId: TransactionId,
    val title: String?,
    val walletName: String,
    val whenLabel: String,
    val amount: Money,
    val type: TransactionType,
    val appearance: Appearance = Appearance("other", "gray"),
)
