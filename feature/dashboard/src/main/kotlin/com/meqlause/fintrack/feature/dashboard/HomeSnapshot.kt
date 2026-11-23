package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Settings
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId

// DOC-03: the latest values of every source Beranda reads, combined once per change
internal data class HomeSnapshot(
    val wallets: List<Wallet>,
    val moneyByWallet: Map<WalletId, Money>,
    val transactions: List<Transaction>,
    val categories: List<Category>,
    val settings: Settings,
)
