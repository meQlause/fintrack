package com.meqlause.fintrack.core.domain.credit

import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.Wallet

/** The wallets, transactions and instalment plans that credit statements are calculated from. */
public data class CreditSnapshot(
    public val wallets: List<Wallet>,
    public val transactions: List<Transaction>,
    public val plans: List<InstallmentPlan>,
)
