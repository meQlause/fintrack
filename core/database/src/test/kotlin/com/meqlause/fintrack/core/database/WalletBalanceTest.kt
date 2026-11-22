package com.meqlause.fintrack.core.database

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.database.entity.TransactionEntity
import com.meqlause.fintrack.core.database.entity.WalletEntity
import com.meqlause.fintrack.core.model.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** DOC-02: the balance query applies the wallet balance formula to every transaction type (BR-004, BR-040) */
@RunWith(RobolectricTestRunner::class)
class WalletBalanceTest {
    private lateinit var database: FintrackDatabase

    /** DOC-02: a fresh in-memory database for each test. */
    @Before
    fun open() {
        database = openInMemoryDatabase()
    }

    /** DOC-02: the in-memory database is closed after each test. */
    @After
    fun close() {
        database.close()
    }

    /** DOC-02: an expense is subtracted from the wallet it leaves. */
    @Test
    fun `expense lowers the wallet balance (DOC-02)`() {
        runBlocking {
            val walletId = insertWallet(initialBalance = 1_000_000L)
            insertMovement(TransactionType.EXPENSE, accounts(walletId), amounts(250_000L))
            assertThat(balancesById()[walletId]).isEqualTo(750_000L)
        }
    }

    /** DOC-02: an income is added to the wallet it enters. */
    @Test
    fun `income raises the wallet balance (DOC-02)`() {
        runBlocking {
            val walletId = insertWallet(initialBalance = 1_000_000L)
            insertMovement(TransactionType.INCOME, accounts(walletId), amounts(300_000L))
            assertThat(balancesById()[walletId]).isEqualTo(1_300_000L)
        }
    }

    /** BR-004: the amount leaves the source, reaches the target, and the fee comes out of the source only. */
    @Test
    fun `transfer moves amount and deducts fee from the source only (BR-004)`() {
        runBlocking {
            val sourceId = insertWallet(initialBalance = 1_000_000L)
            val targetId = insertWallet(initialBalance = 500_000L)
            insertMovement(
                TransactionType.TRANSFER,
                accounts(sourceId, targetWalletId = targetId),
                amounts(200_000L, feeAmount = 5_000L),
            )
            val balances = balancesById()
            assertThat(balances[sourceId]).isEqualTo(795_000L)
            assertThat(balances[targetId]).isEqualTo(700_000L)
        }
    }

    /** BR-040: lending money to someone moves it out of the wallet without any income or expense. */
    @Test
    fun `lend lowers the wallet balance (BR-040)`() {
        runBlocking {
            val walletId = insertWallet(initialBalance = 1_000_000L)
            insertMovement(TransactionType.LEND, accounts(walletId, loanId = insertLoan()), amounts(100_000L))
            assertThat(balancesById()[walletId]).isEqualTo(900_000L)
        }
    }

    /** BR-040: borrowing money from someone moves it into the wallet. */
    @Test
    fun `borrow raises the wallet balance (BR-040)`() {
        runBlocking {
            val walletId = insertWallet(initialBalance = 1_000_000L)
            insertMovement(TransactionType.BORROW, accounts(walletId, loanId = insertLoan()), amounts(150_000L))
            assertThat(balancesById()[walletId]).isEqualTo(1_150_000L)
        }
    }

    /** BR-040: a collection is money coming back into the wallet from the person who owed it. */
    @Test
    fun `collect raises the wallet balance (BR-040)`() {
        runBlocking {
            val walletId = insertWallet(initialBalance = 1_000_000L)
            insertMovement(TransactionType.COLLECT, accounts(walletId, loanId = insertLoan()), amounts(40_000L))
            assertThat(balancesById()[walletId]).isEqualTo(1_040_000L)
        }
    }

    /** BR-040: a repayment is money paid out of the wallet to the person the user borrowed from. */
    @Test
    fun `repay lowers the wallet balance (BR-040)`() {
        runBlocking {
            val walletId = insertWallet(initialBalance = 1_000_000L)
            insertMovement(TransactionType.REPAY, accounts(walletId, loanId = insertLoan()), amounts(60_000L))
            assertThat(balancesById()[walletId]).isEqualTo(940_000L)
        }
    }

    /** BR-043: an archived wallet is still valid history, so its balance is still computed. */
    @Test
    fun `archived wallet keeps its balance in the query (BR-043)`() {
        runBlocking {
            val archived =
                sampleWallet("Lama", initialBalance = 80_000L)
                    .copy(state = WalletEntity.State(isArchived = true))
            val walletId = database.walletDao().insert(archived)
            assertThat(balancesById()[walletId]).isEqualTo(80_000L)
        }
    }

    /** DOC-02: every wallet gets one balance, and the mixed movements of several wallets add up (BR-004, BR-040). */
    @Test
    fun `mixed movements across several wallets give each balance (DOC-02)`() {
        runBlocking {
            val cashId = insertWallet("Tunai", 1_000_000L)
            val bankId = insertWallet("Bank", 2_000_000L)
            val creditId = insertWallet("Kartu", -500_000L)
            val idleId = insertWallet("Diam", 0L)
            insertMovement(TransactionType.EXPENSE, accounts(cashId), amounts(100_000L))
            insertMovement(TransactionType.INCOME, accounts(bankId), amounts(50_000L))
            insertMovement(
                TransactionType.TRANSFER,
                accounts(bankId, targetWalletId = cashId),
                amounts(300_000L, feeAmount = 6_500L),
            )
            insertMovement(TransactionType.TRANSFER, accounts(cashId, targetWalletId = creditId), amounts(200_000L))
            insertMovement(TransactionType.LEND, accounts(cashId, loanId = insertLoan()), amounts(80_000L))
            insertMovement(TransactionType.BORROW, accounts(creditId, loanId = insertLoan()), amounts(120_000L))
            insertMovement(TransactionType.COLLECT, accounts(cashId, loanId = insertLoan()), amounts(30_000L))
            insertMovement(TransactionType.REPAY, accounts(bankId, loanId = insertLoan()), amounts(10_000L))
            val balances = balancesById()
            assertThat(balances[cashId]).isEqualTo(950_000L)
            assertThat(balances[bankId]).isEqualTo(1_733_500L)
            assertThat(balances[creditId]).isEqualTo(-180_000L)
            assertThat(balances[idleId]).isEqualTo(0L)
        }
    }

    /** DOC-02: reads the balance of every wallet from the single query, keyed by wallet id. */
    private suspend fun balancesById(): Map<Long, Long> =
        database
            .walletDao()
            .observeBalances()
            .first()
            .associate { it.walletId to it.balance }

    /** DOC-02: inserts a wallet with the given opening balance and returns its id. */
    private suspend fun insertWallet(
        name: String = "Dompet",
        initialBalance: Long,
    ): Long = database.walletDao().insert(sampleWallet(name, initialBalance))

    /** FR-150: inserts an open loan and returns its id. */
    private suspend fun insertLoan(): Long = database.loanDao().insert(sampleLoan())

    /** DOC-02: inserts one transaction of the given type. */
    private suspend fun insertMovement(
        type: TransactionType,
        accounts: TransactionEntity.Accounts,
        figures: TransactionEntity.Amounts,
    ) {
        database.transactionDao().insert(sampleMovement(type, accounts, figures))
    }

    /** DOC-02: the wallets a movement touches, with optional transfer target and loan. */
    private fun accounts(
        walletId: Long,
        targetWalletId: Long? = null,
        loanId: Long? = null,
    ): TransactionEntity.Accounts =
        TransactionEntity.Accounts(walletId = walletId, targetWalletId = targetWalletId, loanId = loanId)

    /** BR-004: the amount and the transfer fee of a movement. */
    private fun amounts(
        amount: Long,
        feeAmount: Long = 0L,
    ): TransactionEntity.Amounts = TransactionEntity.Amounts(amount = amount, feeAmount = feeAmount)
}
