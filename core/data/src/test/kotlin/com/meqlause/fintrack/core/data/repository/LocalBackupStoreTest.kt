package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.support.LocalRepositoryFixture
import com.meqlause.fintrack.core.data.support.openInMemoryDatabase
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.BackupAdjunct
import com.meqlause.fintrack.core.model.BackupLedger
import com.meqlause.fintrack.core.model.BackupSnapshot
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Preferences
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant

// DOC-11, FR-111: a backup replaces every table, and reading it back returns what was written

/** DOC-11, FR-111: local backup store tests against in-memory Room. */
@RunWith(RobolectricTestRunner::class)
class LocalBackupStoreTest {
    private val database = openInMemoryDatabase()
    private val fixture = LocalRepositoryFixture(database)
    private val store = LocalBackupStore(database)
    private val stamp = Instant.ofEpochMilli(1_700_000_000_000L)

    /** ADR-007: closes the in-memory database after each test. */
    @After
    fun closeDatabase() {
        fixture.close()
    }

    /** FR-111: replacing with an empty snapshot clears every table that was filled before. */
    @Test
    fun givenStoredWalletWhenReplacedWithEmptySnapshotThenTablesAreEmpty() =
        runTest {
            fixture.wallets.insert(bankWallet(WalletId(0L)))
            store.replaceAll(emptySnapshot())
            val (ledger, adjunct) = store.read()
            assertTrue(ledger.wallets.isEmpty())
            assertTrue(ledger.transactions.isEmpty())
            assertTrue(adjunct.loans.isEmpty())
        }

    /** DOC-11: a wallet written by the backup is read back unchanged. */
    @Test
    fun givenSnapshotWithWalletWhenReplacedThenReadReturnsTheSameWallet() =
        runTest {
            val wallet = bankWallet(WalletId(7L))
            val snapshot = emptySnapshot().copy(ledger = emptyLedger().copy(wallets = listOf(wallet)))
            store.replaceAll(snapshot)
            assertEquals(listOf(wallet), store.read().first.wallets)
        }

    private fun emptySnapshot(): BackupSnapshot =
        BackupSnapshot(
            createdAt = stamp,
            ledger = emptyLedger(),
            adjunct =
                BackupAdjunct(
                    monitoredApps = emptyList(),
                    inboxItems = emptyList(),
                    installmentPlans = emptyList(),
                    loans = emptyList(),
                    valuations = emptyList(),
                    quickEntries = emptyList(),
                ),
            preferences = Preferences(),
            backupReminderDays = 7,
        )

    private fun emptyLedger(): BackupLedger =
        BackupLedger(
            wallets = emptyList(),
            categories = emptyList(),
            transactions = emptyList(),
            budgets = emptyList(),
            budgetAlertLogs = emptyList(),
            merchantRules = emptyList(),
        )

    private fun bankWallet(walletId: WalletId): Wallet =
        Wallet(
            walletId = walletId,
            kind = WalletKind(WalletType.BANK, null),
            terms = WalletTerms(Money(150_000L), null),
            profile = WalletProfile("BCA", Appearance("bank", "blue"), 1),
            status = WalletStatus(),
            audit = Audit(stamp, stamp),
        )
}
