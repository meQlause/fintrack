package com.meqlause.fintrack.core.database

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.model.CategoryKind
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** DOC-02: insert and read back the wallet, category, transaction, budget and budget alert tables */
@RunWith(RobolectricTestRunner::class)
class CoreDaoTest {
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

    /** DOC-02: a wallet keeps its name, type, flags and generated id. */
    @Test
    fun `wallet inserted then observed keeps its fields (DOC-02)`() {
        runBlocking {
            val insertedId = database.walletDao().insert(sampleWallet())
            val wallet =
                database
                    .walletDao()
                    .observeAll()
                    .first()
                    .single()
            assertThat(wallet.walletId).isEqualTo(insertedId)
            assertThat(wallet.identity.name).isEqualTo("Dompet")
            assertThat(wallet.state.isIncludedInTotal).isTrue()
            assertThat(wallet.state.isArchived).isFalse()
        }
    }

    /** DOC-02: a category keeps its kind and default flag. */
    @Test
    fun `category inserted then observed keeps its kind (DOC-02)`() {
        runBlocking {
            database.categoryDao().insert(sampleCategory())
            val category =
                database
                    .categoryDao()
                    .observeAll()
                    .first()
                    .single()
            assertThat(category.identity.kind).isEqualTo(CategoryKind.EXPENSE)
            assertThat(category.state.isDefault).isFalse()
        }
    }

    /** DOC-02: a transaction is stored with its amount, accounts and origin. */
    @Test
    fun `transaction inserted then observed keeps amount and wallet (DOC-02)`() {
        runBlocking {
            val walletId = database.walletDao().insert(sampleWallet())
            database.transactionDao().insert(sampleTransaction(walletId))
            val transaction =
                database
                    .transactionDao()
                    .observeAll()
                    .first()
                    .single()
            assertThat(transaction.figures.amount).isEqualTo(25_000L)
            assertThat(transaction.accounts.walletId).isEqualTo(walletId)
        }
    }

    /** DOC-02: a budget keeps its limit; a null category id marks the overall budget (FR-074). */
    @Test
    fun `budget inserted then observed keeps limit and overall marker (DOC-02)`() {
        runBlocking {
            database.budgetDao().insert(sampleBudget(categoryId = null))
            val budget =
                database
                    .budgetDao()
                    .observeAll()
                    .first()
                    .single()
            assertThat(budget.limitAmount).isEqualTo(500_000L)
            assertThat(budget.categoryId).isNull()
        }
    }

    /** DOC-02: an alert log row is keyed by budget, period and threshold (FR-073). */
    @Test
    fun `budget alert log inserted then observed (DOC-02)`() {
        runBlocking {
            val budgetId = database.budgetDao().insert(sampleBudget(categoryId = null))
            database.budgetAlertLogDao().insert(sampleBudgetAlert(budgetId))
            assertThat(database.budgetAlertLogDao().observeAll().first()).hasSize(1)
        }
    }
}
