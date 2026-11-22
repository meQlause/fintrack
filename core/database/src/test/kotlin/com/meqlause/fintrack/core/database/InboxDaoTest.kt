package com.meqlause.fintrack.core.database

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.model.InboxStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** DOC-02: insert and read back the inbox, merchant memory and monitored app tables */
@RunWith(RobolectricTestRunner::class)
class InboxDaoTest {
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

    /** DOC-02: a detected notification starts PENDING with its fingerprint (FR-090). */
    @Test
    fun `inbox item inserted then observed starts pending (DOC-02)`() {
        runBlocking {
            database.inboxItemDao().insert(sampleInboxItem())
            val item =
                database
                    .inboxItemDao()
                    .observeAll()
                    .first()
                    .single()
            assertThat(item.lifecycle.status).isEqualTo(InboxStatus.PENDING)
            assertThat(item.lifecycle.fingerprint).isEqualTo("fp-1")
        }
    }

    /** DOC-02: a merchant rule links a normalized merchant key to a category (FR-088). */
    @Test
    fun `merchant rule inserted then observed (DOC-02)`() {
        runBlocking {
            val categoryId = database.categoryDao().insert(sampleCategory())
            database.merchantRuleDao().insert(sampleMerchantRule(categoryId))
            val rule =
                database
                    .merchantRuleDao()
                    .observeAll()
                    .first()
                    .single()
            assertThat(rule.merchantKey).isEqualTo("kopi kenangan")
            assertThat(rule.categoryId).isEqualTo(categoryId)
        }
    }

    /** DOC-02: a monitored app is keyed by its package name and can be disabled. */
    @Test
    fun `monitored app inserted then observed (DOC-02)`() {
        runBlocking {
            database.monitoredAppDao().insert(sampleMonitoredApp())
            val monitoredApp =
                database
                    .monitoredAppDao()
                    .observeAll()
                    .first()
                    .single()
            assertThat(monitoredApp.packageName).isEqualTo("com.bank.app")
            assertThat(monitoredApp.isEnabled).isTrue()
        }
    }
}
