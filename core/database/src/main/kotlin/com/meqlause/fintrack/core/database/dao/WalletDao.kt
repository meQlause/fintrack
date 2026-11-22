package com.meqlause.fintrack.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.meqlause.fintrack.core.database.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

/** DOC-02: wallet writes are suspend, reads are Flow (03 threading). */
@Dao
interface WalletDao {
    /** DOC-02: inserts a wallet and returns its generated id. */
    @Insert
    suspend fun insert(wallet: WalletEntity): Long

    /** BR-008: fails with a constraint violation while any row still references the wallet. */
    @Delete
    suspend fun delete(wallet: WalletEntity)

    /** DOC-02: every wallet in manual order, archived ones included. */
    @Query("SELECT * FROM wallet ORDER BY sortOrder")
    fun observeAll(): Flow<List<WalletEntity>>

    /**
     * DOC-02, BR-004, BR-040: the balance of every wallet, archived ones included, in manual order.
     * One statement applies the formula in 02: opening balance, plus incoming INCOME, TRANSFER targets,
     * BORROW and COLLECT, minus outgoing EXPENSE, TRANSFER sources (amount and fee), LEND and REPAY.
     */
    @Query(WALLET_BALANCE_SQL)
    fun observeBalances(): Flow<List<WalletBalance>>
}

// DOC-02, BR-004, BR-040: each sum is a correlated subquery over the transaction table
private const val WALLET_BALANCE_SQL =
    "SELECT w.walletId AS walletId, " +
        "w.initialBalance " +
        "+ (SELECT COALESCE(SUM(t.amount), 0) FROM `transaction` t " +
        "WHERE t.walletId = w.walletId AND t.type = 'INCOME') " +
        "- (SELECT COALESCE(SUM(t.amount), 0) FROM `transaction` t " +
        "WHERE t.walletId = w.walletId AND t.type = 'EXPENSE') " +
        "- (SELECT COALESCE(SUM(t.amount + t.feeAmount), 0) FROM `transaction` t " +
        "WHERE t.walletId = w.walletId AND t.type = 'TRANSFER') " +
        "+ (SELECT COALESCE(SUM(t.amount), 0) FROM `transaction` t " +
        "WHERE t.targetWalletId = w.walletId AND t.type = 'TRANSFER') " +
        "- (SELECT COALESCE(SUM(t.amount), 0) FROM `transaction` t " +
        "WHERE t.walletId = w.walletId AND t.type IN ('LEND', 'REPAY')) " +
        "+ (SELECT COALESCE(SUM(t.amount), 0) FROM `transaction` t " +
        "WHERE t.walletId = w.walletId AND t.type IN ('BORROW', 'COLLECT')) AS balance " +
        "FROM wallet w ORDER BY w.sortOrder"
