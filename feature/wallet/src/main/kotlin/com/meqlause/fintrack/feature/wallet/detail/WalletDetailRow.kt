package com.meqlause.fintrack.feature.wallet.detail

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType

// FR-017, S-19: one recent transaction of the wallet, already formatted for a TransactionRow
internal data class WalletDetailRow(
    val transactionId: Long,
    val title: String,
    val subtitle: String,
    val amount: Money,
    val type: TransactionType,
)
