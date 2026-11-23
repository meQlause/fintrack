package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.StatementStatus
import com.meqlause.fintrack.core.model.WalletId

// FR-137, S-03: one credit bill row of the Tagihan card with its amount, status and due date already computed
internal data class HomeBill(
    val walletId: WalletId,
    val name: String,
    val status: StatementStatus,
    val amount: Money,
    val dateLabel: String,
)
