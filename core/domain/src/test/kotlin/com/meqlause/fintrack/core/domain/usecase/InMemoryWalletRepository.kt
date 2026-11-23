package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** FR-010: in-memory wallets for the domain tests; core:testing cannot be used from this pure JVM module. */
internal class InMemoryWalletRepository : WalletRepository {
    private val rows = MutableStateFlow<List<Wallet>>(emptyList())
    private var lastId = 0L

    override fun observeAll(): Flow<List<Wallet>> = rows

    override fun observeBalances(): Flow<Map<WalletId, Money>> =
        rows.map { list -> list.associate { it.walletId to Money(0L) } }

    override suspend fun insert(wallet: Wallet): WalletId {
        lastId += 1
        val stored = wallet.copy(walletId = WalletId(lastId))
        rows.value = rows.value + stored
        return stored.walletId
    }

    override suspend fun delete(wallet: Wallet) {
        rows.value = rows.value.filterNot { it.walletId == wallet.walletId }
    }
}
