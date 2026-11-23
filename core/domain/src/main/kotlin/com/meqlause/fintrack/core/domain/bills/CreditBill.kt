package com.meqlause.fintrack.core.domain.bills

import com.meqlause.fintrack.core.domain.credit.StatementSummary
import com.meqlause.fintrack.core.model.WalletId

/** FR-137: one credit wallet on the Tagihan card with the statement that is due next */
public data class CreditBill(
    public val walletId: WalletId,
    public val walletName: String,
    public val statement: StatementSummary,
)
