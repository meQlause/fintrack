package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * ADR-007, FR-010: in-memory wallets. Balances are computed from the transactions of [transactions],
 * the same way the database computes them (BR-004, BR-040).
 */
public class FakeWalletRepository(
    private val transactions: FakeTransactionRepository = FakeTransactionRepository(),
) : WalletRepository {
    private val store =
        RowStore<Wallet>(
            idOf = { it.walletId.value },
            withId = { wallet, newId -> wallet.copy(walletId = WalletId(newId)) },
            order = compareBy { it.profile.sortOrder },
        )

    override fun observeAll(): Flow<List<Wallet>> = store.observe()

    override fun observeBalances(): Flow<Map<WalletId, Money>> =
        combine(store.observe(), transactions.observeAll()) { wallets, allTransactions ->
            wallets.associate { it.walletId to balanceOf(it, allTransactions) }
        }

    override suspend fun insert(wallet: Wallet): WalletId = WalletId(store.append(wallet))

    override suspend fun delete(wallet: Wallet) {
        store.remove(wallet.walletId.value)
    }

    private fun balanceOf(
        wallet: Wallet,
        allTransactions: List<Transaction>,
    ): Money = Money(wallet.terms.initialBalance.rupiah + allTransactions.sumOf { it.effectOn(wallet.walletId) })

    // DOC-02, BR-004, BR-040: the signed effect of one transaction on the balance of one wallet
    private fun Transaction.effectOn(walletId: WalletId): Long {
        val amount = movement.amount.rupiah
        val isSource = routing.walletId == walletId
        val isTarget = routing.targetWalletId == walletId
        return when (movement.type) {
            TransactionType.INCOME, TransactionType.BORROW, TransactionType.COLLECT -> if (isSource) amount else 0L
            TransactionType.EXPENSE, TransactionType.LEND, TransactionType.REPAY -> if (isSource) -amount else 0L
            TransactionType.TRANSFER -> transferEffect(amount, isSource, isTarget)
        }
    }

    // BR-004: the source pays amount plus fee, the target receives amount
    private fun Transaction.transferEffect(
        amount: Long,
        isSource: Boolean,
        isTarget: Boolean,
    ): Long {
        val outgoing = if (isSource) -(amount + movement.feeAmount.rupiah) else 0L
        val incoming = if (isTarget) amount else 0L
        return outgoing + incoming
    }
}
