package com.meqlause.fintrack.core.database

import android.database.sqlite.SQLiteConstraintException
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** DOC-02: foreign keys are RESTRICT, so a referenced wallet or category cannot be deleted (BR-008) */
@RunWith(RobolectricTestRunner::class)
class ForeignKeyRestrictTest {
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

    /** BR-008: a wallet still referenced by a transaction cannot be deleted; it must be archived instead. */
    @Test
    fun `deleting a wallet referenced by a transaction fails (BR-008)`() {
        runBlocking<Unit> {
            val wallet = sampleWallet()
            val walletId = database.walletDao().insert(wallet)
            database.transactionDao().insert(sampleTransaction(walletId))
            val failure =
                runCatching {
                    database.walletDao().delete(
                        wallet.copy(walletId = walletId),
                    )
                }.exceptionOrNull()
            assertThat(failure).isNotNull().isInstanceOf(SQLiteConstraintException::class.java)
        }
    }

    /** BR-008: a category still referenced by a transaction cannot be deleted; it must be archived instead. */
    @Test
    fun `deleting a category referenced by a transaction fails (BR-008)`() {
        runBlocking<Unit> {
            val walletId = database.walletDao().insert(sampleWallet())
            val category = sampleCategory()
            val categoryId = database.categoryDao().insert(category)
            database.transactionDao().insert(sampleTransaction(walletId, categoryId))
            val failure =
                runCatching { database.categoryDao().delete(category.copy(categoryId = categoryId)) }.exceptionOrNull()
            assertThat(failure).isNotNull().isInstanceOf(SQLiteConstraintException::class.java)
        }
    }

    /** BR-008: an unreferenced wallet is still deletable, so the restriction only guards real references. */
    @Test
    fun `deleting an unreferenced wallet succeeds (BR-008)`() {
        runBlocking {
            val wallet = sampleWallet()
            val walletId = database.walletDao().insert(wallet)
            database.walletDao().delete(wallet.copy(walletId = walletId))
            assertThat(database.walletDao().observeAll().first()).hasSize(0)
        }
    }
}
