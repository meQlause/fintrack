package com.meqlause.fintrack.core.domain.investment

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId
import java.time.Instant

/** FR-173: one timeline entry of an investment wallet; amount is the transfer amount or the valued amount. */
public data class InvestmentActivity(
    public val walletId: WalletId,
    public val occurredAt: Instant,
    public val kind: ActivityKind,
    public val amount: Money,
)
