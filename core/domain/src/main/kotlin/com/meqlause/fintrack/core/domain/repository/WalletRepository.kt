package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.Flow

/** DOC-03, FR-010: access to wallets and their computed balances (BR-004, BR-043). */
public interface WalletRepository {
    /** FR-010: every wallet in manual order, archived ones included. */
    public fun observeAll(): Flow<List<Wallet>>

    /** BR-004: the balance of every wallet, computed from its transactions and never stored. */
    public fun observeBalances(): Flow<Map<WalletId, Money>>

    /** FR-010: stores a wallet and returns its generated id; the walletId of [wallet] is ignored. */
    public suspend fun insert(wallet: Wallet): WalletId

    /** BR-008: removes a wallet that no transaction refers to. */
    public suspend fun delete(wallet: Wallet)
}
