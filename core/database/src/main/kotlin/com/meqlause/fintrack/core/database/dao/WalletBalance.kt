package com.meqlause.fintrack.core.database.dao

/** DOC-02, BR-004: one wallet's balance in whole rupiah, computed by WalletDao.observeBalances(), never stored. */
data class WalletBalance(
    val walletId: Long,
    val balance: Long,
)
