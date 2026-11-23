package com.meqlause.fintrack.core.domain.networth

import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId

// BR-043: the stored rows the net worth reads, observed together so one emission carries one consistent set
internal data class NetWorthLedger(
    val wallets: List<Wallet>,
    val moneyByWallet: Map<WalletId, Money>,
    val transactions: List<Transaction>,
    val plans: List<InstallmentPlan>,
)
