package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.mapper.toEntity
import com.meqlause.fintrack.core.data.mapper.toWallet
import com.meqlause.fintrack.core.database.dao.WalletDao
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** DOC-03, ADR-007, FR-010: wallets in Room; balances come from the single statement in WalletDao (BR-004). */
public class LocalWalletRepository(
    private val walletDao: WalletDao,
) : WalletRepository {
    override fun observeAll(): Flow<List<Wallet>> = walletDao.observeAll().map { rows -> rows.map { it.toWallet() } }

    override fun observeBalances(): Flow<Map<WalletId, Money>> =
        walletDao.observeBalances().map { rows ->
            rows.associate { WalletId(it.walletId) to Money(it.balance) }
        }

    override suspend fun insert(wallet: Wallet): WalletId = WalletId(walletDao.insert(wallet.toEntity()))

    override suspend fun delete(wallet: Wallet) {
        walletDao.delete(wallet.toEntity())
    }
}
