package com.meqlause.fintrack.core.database

import assertk.assertThat
import assertk.assertions.isEmpty
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** DOC-02: valuation history belongs to its investment wallet, so it is deleted with the wallet (ADR-017). */
@RunWith(RobolectricTestRunner::class)
class ValuationCascadeTest {
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

    /** DOC-02: valuation_entry.walletId is CASCADE, the one exception to RESTRICT besides budget alerts. */
    @Test
    fun `deleting an investment wallet deletes its valuations (DOC-02, ADR-017)`() {
        runBlocking {
            val wallet = sampleWallet()
            val walletId = database.walletDao().insert(wallet)
            database.valuationEntryDao().insert(sampleValuation(walletId))

            database.walletDao().delete(wallet.copy(walletId = walletId))

            assertThat(database.valuationEntryDao().observeAll().first()).isEmpty()
        }
    }
}
